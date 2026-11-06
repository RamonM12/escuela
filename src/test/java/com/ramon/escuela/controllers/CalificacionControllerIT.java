package com.ramon.escuela.controllers;

import com.ramon.escuela.entities.*;
import com.ramon.escuela.repositories.*;
import com.ramon.escuela.services.Calificacion.CalificacionService;
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
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "server.port=0",
        "spring.datasource.url=jdbc:h2:mem:pruebasCalificacion;MODE=Oracle;DB_CLOSE_DELAY=-1",
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
class CalificacionControllerIT {
    private static final String RUTA = "/api/calificaciones";
    @Autowired MockMvc mvc;
    @Autowired EntityManager entityManager;
    @MockitoSpyBean CalificacionService service;
    @Autowired MaestroRepository maestroRepository;
    @Autowired AulaRepository aulaRepository;
    @Autowired CursoRepository cursoRepository;
    @Autowired GrupoRepository grupoRepository;
    @Autowired AlumnoRepository alumnoRepository;
    @Autowired InscripcionRepository inscripcionRepository;
    @Autowired CalificacionRepository calificacionRepository;
    Maestro maestro;
    Aula aula;
    Curso curso;
    Grupo grupo;
    Alumno alumno;
    Inscripcion inscripcion;

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
        inscribir(grupoOtroPeriodo());
        inscripcion = inscribir(grupo);
        entityManager.clear();
    }

    @Test
    void registrar_datosValidos_registraCalificacion() throws Exception {
        // Arrange
        String json = requestValido();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(post(RUTA).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNumber()).andExpect(jsonPath("$.calificacion").value(8.5));
    }

    @Test
    void registrar_inscripcionYaCalificada_lanzaConflictoException() throws Exception {
        // Arrange
        prepararEntidad();
        String json = requestValido();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(post(RUTA).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().is(409))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void registrar_inscripcionInexistente_lanzaRecursoNoEncontradoException() throws Exception {
        // Arrange
        String json = "{\"idInscripcion\":%d,\"calificacion\":8.5}".formatted(999999L);
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(post(RUTA).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().is(404))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void registrar_calificacionNula_retorna400() throws Exception {
        // Arrange
        String json = "{\"idInscripcion\":%d,\"calificacion\":null}".formatted(inscripcion.getId());
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
    void obtenerPorId_calificacionExistente_retornaCalificacion() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(get(RUTA + "/{id}",id));

        // Assert
        resultado.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id)).andExpect(jsonPath("$.calificacion").value(8.5));
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
    void listar_conCalificaciones_retornaLista() throws Exception {
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
    void actualizar_valorValido_actualizaCalificacion() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        String json = "{\"idInscripcion\":%d,\"calificacion\":9}".formatted(inscripcion.getId());
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(put(RUTA + "/{id}",id).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.calificacion").value(9))
                .andExpect(jsonPath("$.inscripcion.grupo.periodo").value("2026-01"));
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
    void eliminar_calificacionExistente_eliminaCalificacion() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(delete(RUTA + "/{id}",id));

        // Assert
        resultado.andExpect(status().isNoContent());
        assertThat(calificacionRepository.existsById(id)).isFalse();
    }

    @Test
    void eliminar_idInexistente_lanzaRecursoNoEncontradoException() throws Exception {
        // Arrange
        long id = 999999L;
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(delete(RUTA + "/{id}",id));

        // Assert
        resultado.andExpect(status().is(404))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    private String requestValido() {
        return "{\"idInscripcion\":%d,\"calificacion\":8.5}".formatted(inscripcion.getId());
    }

    private Long prepararEntidad() {
        return calificar(inscripcion).getId();
    }

    private Calificacion calificar(Inscripcion registro) {
        Calificacion nota = Calificacion.crear(new BigDecimal("8.5"),registro);
        return calificacionRepository.saveAndFlush(nota);
    }
    
    private Inscripcion inscribir(Grupo destino) {
        Inscripcion registro = Inscripcion.crear(alumno,destino);
        return inscripcionRepository.saveAndFlush(registro);
    }
    
    private Grupo grupoOtroPeriodo() {
        Grupo otro = Grupo.crear(curso,maestro,aula,"2026-02");
        return grupoRepository.saveAndFlush(otro);
    }
}
