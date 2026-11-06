package com.ramon.escuela.controllers;

import com.ramon.escuela.entities.*;
import com.ramon.escuela.repositories.*;
import com.ramon.escuela.services.alumnos.AlumnoService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "server.port=0",
        "spring.datasource.url=jdbc:h2:mem:pruebasAlumno;MODE=Oracle;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.open-in-view=false",
        "logging.level.root=WARN",
        "spring.main.banner-mode=off"
})
@AutoConfigureMockMvc
@Transactional
class AlumnoControllerIT {
    private static final String RUTA = "/api/alumnos";
    @Autowired MockMvc mvc;
    @Autowired EntityManager entityManager;
    @MockitoSpyBean AlumnoService service;
    @Autowired MaestroRepository maestroRepository;
    @Autowired AulaRepository aulaRepository;
    @Autowired CursoRepository cursoRepository;
    @Autowired GrupoRepository grupoRepository;
    @MockitoSpyBean AlumnoRepository alumnoRepository;
    @Autowired InscripcionRepository inscripcionRepository;
    @Autowired CalificacionRepository calificacionRepository;
    Maestro maestro;
    Aula aula;
    Curso curso;
    Grupo grupo;
    Alumno alumno;

    @BeforeEach
    void prepararDatos() {
        maestro = maestroRepository.saveAndFlush(Maestro.crear("Laura","Martinez","Lopez","laura@escuela.com","5512345678"));
        aula = aulaRepository.saveAndFlush(Aula.crear("Aula 101",30));
        curso = cursoRepository.saveAndFlush(Curso.crear("Matematicas I","Fundamentos",6));
        grupo = Grupo.crear(curso,maestro,aula,"2026-01");
        grupoRepository.saveAndFlush(grupo);
        alumno = Alumno.crear("Juan","Perez","Lopez");
        alumno.asignarDatosAcademicos("juan@escuela.com","PELOJU2601");
        alumnoRepository.saveAndFlush(alumno);
        clearInvocations(alumnoRepository);
        doAnswer(invocacion -> String.format("%010d",Integer.toUnsignedLong(java.util.Objects.hash(invocacion.getArgument(0),invocacion.getArgument(1),invocacion.getArgument(2)))))
                .when(alumnoRepository).generarMatricula(anyString(),anyString(),anyString());
        doAnswer(invocacion -> "alumno." + Integer.toUnsignedLong(java.util.Objects.hash(invocacion.getArgument(0),invocacion.getArgument(1),invocacion.getArgument(2))) + "@escuela.com")
                .when(alumnoRepository).generarEmail(anyString(),anyString(),anyString());
        entityManager.clear();
    }

    @Test
    void registrar_datosValidos_generaMatriculaYEmail() throws Exception {
        // Arrange
        String json = requestValido();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(post(RUTA).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNumber()).andExpect(jsonPath("$.matricula").value(matchesPattern("[0-9]{10}")));
    }

    @Test
    void registrar_nombreVacio_lanzaDatoInvalidoException() throws Exception {
        // Arrange
        String json = "{\"nombre\":\" \",\"apellidoPaterno\":\"Perez\",\"apellidoMaterno\":\"Lopez\"}";
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(post(RUTA).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().is(400))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty())
                .andExpect(jsonPath("$.errores").isArray());
    }

    @Test
    void obtenerPorId_alumnoExistente_retornaAlumnoConPromedio() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(get(RUTA + "/{id}",id));

        // Assert
        resultado.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id)).andExpect(jsonPath("$.promedio").value(0));
    }

    @Test
    void obtenerPorId_sinCalificaciones_retornaPromedioCero() throws Exception {
        // Arrange
        Long id = alumno.getId();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(get(RUTA + "/{id}",id));

        // Assert
        resultado.andExpect(status().isOk()).andExpect(jsonPath("$.promedio").value(0));
    }

    @Test
    void obtenerPorId_conCalificacionesNulas_ignoraNulosEnPromedio() throws Exception {
        // Arrange
        calificar(inscribir(grupo));
        inscribir(grupoOtroPeriodo());
        Long id = alumno.getId();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(get(RUTA + "/{id}",id));

        // Assert
        resultado.andExpect(status().isOk()).andExpect(jsonPath("$.promedio").value(8.5));
    }

    @Test
    void obtenerPorId_idInexistente_lanzaRecursoNoEncontradoException() throws Exception {
        // Arrange
        long id = 999999L;
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(get(RUTA + "/{id}",id));

        // Assert
        resultado.andExpect(status().is(404))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void listar_conAlumnos_retornaLista() throws Exception {
        // Arrange
        prepararEntidad();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(get(RUTA));

        // Assert
        resultado.andExpect(status().isOk()).andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void listar_errorDeRepositorio_propagaExcepcion() throws Exception {
        // Arrange
        doThrow(new IllegalStateException("fallo simulado")).when(service).listar();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(get(RUTA));

        // Assert
        resultado.andExpect(status().is(500))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void actualizar_cambiaNombre_regeneraMatriculaYEmail() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        String anterior = alumno.getMatricula();
        String json = requestValido();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(put(RUTA + "/{id}",id).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.matricula").value(not(anterior)));
    }

    @Test
    void actualizar_cambiaSoloApellidoMaterno_regeneraMatriculaYEmail() throws Exception {
        // Arrange
        Long id = alumno.getId();
        String matriculaAnterior = alumno.getMatricula();
        String emailAnterior = alumno.getEmail();
        String json = "{\"nombre\":\"Juan\",\"apellidoPaterno\":\"Perez\",\"apellidoMaterno\":\"Ramos\"}";
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(put(RUTA + "/{id}",id).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().isOk())
                .andExpect(jsonPath("$.matricula").value(not(matriculaAnterior)))
                .andExpect(jsonPath("$.email").value(not(emailAnterior)));
    }

    @Test
    void actualizar_sinCambiosEnDatos_noRegeneraNiGuarda() throws Exception {
        // Arrange
        Long id = alumno.getId();
        String anterior = alumno.getMatricula();
        String json = "{\"nombre\":\"Juan\",\"apellidoPaterno\":\"Perez\",\"apellidoMaterno\":\"Lopez\"}";
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(put(RUTA + "/{id}",id).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().isOk()).andExpect(jsonPath("$.matricula").value(anterior));
        verify(alumnoRepository,never()).generarEmail(anyString(),anyString(),anyString());
        verify(alumnoRepository,never()).generarMatricula(anyString(),anyString(),anyString());
        verify(alumnoRepository,never()).saveAndFlush(any());
    }

    @Test
    void actualizar_idInexistente_lanzaRecursoNoEncontradoException() throws Exception {
        // Arrange
        long id = 999999L;
        String json = requestValido();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(put(RUTA + "/{id}",id).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().is(404))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void eliminar_sinInscripciones_eliminaAlumno() throws Exception {
        // Arrange
        Long id = alumno.getId();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(delete(RUTA + "/{id}",id));

        // Assert
        resultado.andExpect(status().isNoContent());
        assertThat(alumnoRepository.existsById(id)).isFalse();
    }

    @Test
    void eliminar_conInscripciones_lanzaEntidadRelacionadaException() throws Exception {
        // Arrange
        inscribir(grupo);
        Long id = alumno.getId();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(delete(RUTA + "/{id}",id));

        // Assert
        resultado.andExpect(status().is(409))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    private String requestValido() {
        return "{\"nombre\":\"Maria\",\"apellidoPaterno\":\"Gomez\",\"apellidoMaterno\":\"Ramos\"}";
    }

    private Long prepararEntidad() {
        return alumno.getId();
    }

    private Inscripcion inscribir(Grupo destino) {
        Inscripcion registro = Inscripcion.crear(alumno,destino);
        return inscripcionRepository.saveAndFlush(registro);
    }
    
    private Calificacion calificar(Inscripcion registro) {
        Calificacion nota = Calificacion.crear(new BigDecimal("8.5"),registro);
        return calificacionRepository.saveAndFlush(nota);
    }
    
    private Grupo grupoOtroPeriodo() {
        Grupo otro = Grupo.crear(curso,maestro,aula,"2026-02");
        return grupoRepository.saveAndFlush(otro);
    }
}
