package com.ramon.escuela.controllers;

import com.ramon.escuela.entities.*;
import com.ramon.escuela.repositories.*;
import com.ramon.escuela.services.grupo.GrupoService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "server.port=0",
        "spring.datasource.url=jdbc:h2:mem:pruebasGrupo;MODE=Oracle;DB_CLOSE_DELAY=-1",
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
class GrupoControllerIT {
    private static final String RUTA = "/api/grupos";
    @Autowired MockMvc mvc;
    @Autowired EntityManager entityManager;
    @MockitoSpyBean GrupoService service;
    @Autowired MaestroRepository maestroRepository;
    @Autowired AulaRepository aulaRepository;
    @Autowired CursoRepository cursoRepository;
    @Autowired GrupoRepository grupoRepository;
    @Autowired AlumnoRepository alumnoRepository;
    @Autowired HorarioRepository horarioRepository;
    @Autowired InscripcionRepository inscripcionRepository;
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
        entityManager.clear();
    }

    @Test
    void registrar_datosValidos_registraGrupo() throws Exception {
        // Arrange
        String json = requestValido();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(post(RUTA).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNumber()).andExpect(jsonPath("$.periodo").value("2026-02"));
    }

    @Test
    void registrar_periodoFormatoValido_registraGrupo() throws Exception {
        // Arrange
        String json = requestValido();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(post(RUTA).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().isCreated()).andExpect(jsonPath("$.periodo").value("2026-02"));
    }

    @Test
    void registrar_combinacionDuplicada_lanzaConflictoException() throws Exception {
        // Arrange
        String json = "{\"idCurso\":%d,\"idMaestro\":%d,\"idAula\":%d,\"periodo\":\"2026-01\"}".formatted(curso.getId(),maestro.getId(),aula.getId());
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
    void registrar_periodoFormatoInvalido_retorna400() throws Exception {
        // Arrange
        String json = "{\"idCurso\":%d,\"idMaestro\":%d,\"idAula\":%d,\"periodo\":\"2026-13\"}".formatted(curso.getId(),maestro.getId(),aula.getId());
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
    void registrar_cursoInexistente_lanzaRecursoNoEncontradoException() throws Exception {
        // Arrange
        String json = "{\"idCurso\":%d,\"idMaestro\":%d,\"idAula\":%d,\"periodo\":\"2026-02\"}".formatted(999999L,maestro.getId(),aula.getId());
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
    void obtenerPorId_grupoExistente_retornaGrupoConHorarios() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(get(RUTA + "/{id}",id));

        // Assert
        resultado.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id)).andExpect(jsonPath("$.horarios[0]").value("Lunes 08:00 - 10:00"));
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
    void listar_conGrupos_retornaLista() throws Exception {
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
    void actualizar_datosValidos_actualizaGrupo() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        String json = "{\"idCurso\":%d,\"idMaestro\":%d,\"idAula\":%d,\"periodo\":\"2026-01\"}".formatted(curso.getId(),maestro.getId(),aula.getId());
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(put(RUTA + "/{id}",id).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void actualizar_combinacionDeOtroGrupo_lanzaConflictoException() throws Exception {
        // Arrange
        Long id = grupoOtroPeriodo().getId();
        String json = "{\"idCurso\":%d,\"idMaestro\":%d,\"idAula\":%d,\"periodo\":\"2026-01\"}".formatted(curso.getId(),maestro.getId(),aula.getId());
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(put(RUTA + "/{id}",id).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().is(409))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void actualizar_periodoFormatoInvalido_retorna400() throws Exception {
        // Arrange
        Long id = grupo.getId();
        String json = "{\"idCurso\":%d,\"idMaestro\":%d,\"idAula\":%d,\"periodo\":\"2026-1\"}".formatted(curso.getId(),maestro.getId(),aula.getId());
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(put(RUTA + "/{id}",id).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().is(400))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty())
                .andExpect(jsonPath("$.errores").isArray());
    }

    @Test
    void eliminar_sinInscripcionesNiHorarios_eliminaGrupo() throws Exception {
        // Arrange
        Long id = grupo.getId();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(delete(RUTA + "/{id}",id));

        // Assert
        resultado.andExpect(status().isNoContent());
        assertThat(grupoRepository.existsById(id)).isFalse();
    }

    @Test
    void eliminar_conInscripciones_lanzaEntidadRelacionadaException() throws Exception {
        // Arrange
        inscribir(grupo);
        Long id = grupo.getId();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(delete(RUTA + "/{id}",id));

        // Assert
        resultado.andExpect(status().is(409))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void eliminar_conHorarios_lanzaEntidadRelacionadaException() throws Exception {
        // Arrange
        guardarHorario(grupo,"08:00","10:00");
        Long id = grupo.getId();
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
        return "{\"idCurso\":%d,\"idMaestro\":%d,\"idAula\":%d,\"periodo\":\"2026-02\"}".formatted(curso.getId(),maestro.getId(),aula.getId());
    }

    private Long prepararEntidad() {
        guardarHorario(grupo,"08:00","10:00");
        return grupo.getId();
    }

    private Inscripcion inscribir(Grupo destino) {
        Inscripcion registro = Inscripcion.crear(alumno,destino);
        return inscripcionRepository.saveAndFlush(registro);
    }
    
    private Horario guardarHorario(Grupo destino,String inicio,String fin) {
        Horario horario = Horario.crear(destino,com.ramon.escuela.enums.DiaSemana.LUNES,inicio,fin);
        return horarioRepository.saveAndFlush(horario);
    }
    
    private Grupo grupoOtroPeriodo() {
        Grupo otro = Grupo.crear(curso,maestro,aula,"2026-02");
        return grupoRepository.saveAndFlush(otro);
    }
}
