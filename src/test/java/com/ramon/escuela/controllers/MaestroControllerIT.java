package com.ramon.escuela.controllers;

import com.ramon.escuela.entities.Aula;
import com.ramon.escuela.entities.Curso;
import com.ramon.escuela.entities.Grupo;
import com.ramon.escuela.entities.Maestro;
import com.ramon.escuela.repositories.AulaRepository;
import com.ramon.escuela.repositories.CursoRepository;
import com.ramon.escuela.repositories.GrupoRepository;
import com.ramon.escuela.repositories.MaestroRepository;
import com.ramon.escuela.services.maestros.MaestroService;
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
        "spring.datasource.url=jdbc:h2:mem:pruebasMaestro;MODE=Oracle;DB_CLOSE_DELAY=-1",
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
class MaestroControllerIT {
    private static final String RUTA = "/api/maestros";
    @Autowired MockMvc mvc;
    @Autowired EntityManager entityManager;
    @MockitoSpyBean MaestroService service;
    @Autowired MaestroRepository maestroRepository;
    @Autowired AulaRepository aulaRepository;
    @Autowired CursoRepository cursoRepository;
    @Autowired GrupoRepository grupoRepository;
    Maestro maestro;
    Aula aula;
    Curso curso;
    Grupo grupo;

    @BeforeEach
    void prepararDatos() {
        maestro = maestroRepository.saveAndFlush(Maestro.crear("Laura","Martinez","Lopez","laura@escuela.com","5512345678"));
        aula = aulaRepository.saveAndFlush(Aula.crear("Aula 101",30));
        curso = cursoRepository.saveAndFlush(Curso.crear("Matematicas I","Fundamentos",6));
        grupo = Grupo.crear(curso,maestro,aula,"2026-01");
        grupoRepository.saveAndFlush(grupo);
        entityManager.clear();
    }

    @Test
    void registrar_datosValidos_registraMaestro() throws Exception {
        // Arrange
        String json = requestValido();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(post(RUTA).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNumber()).andExpect(jsonPath("$.email").value("ana@escuela.com"));
    }

    @Test
    void registrar_emailDuplicado_lanzaConflictoException() throws Exception {
        // Arrange
        String json = "{\"nombre\":\"Ana\",\"apellidoPaterno\":\"Garcia\",\"apellidoMaterno\":\"Ramos\",\"email\":\"laura@escuela.com\",\"telefono\":\"5598765432\"}";
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
    void registrar_telefonoDuplicado_lanzaConflictoException() throws Exception {
        // Arrange
        String json = "{\"nombre\":\"Ana\",\"apellidoPaterno\":\"Garcia\",\"apellidoMaterno\":\"Ramos\",\"email\":\"ana@escuela.com\",\"telefono\":\"5512345678\"}";
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
    void obtenerPorId_maestroExistente_retornaMaestroConCursos() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(get(RUTA + "/{id}",id));

        // Assert
        resultado.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id)).andExpect(jsonPath("$.cursos[0].nombre").value("Matematicas I"));
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
    void listar_conMaestros_retornaLista() throws Exception {
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
    void actualizar_mismoEmailYTelefonoPropios_noLanzaConflicto() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        String json = "{\"nombre\":\"Laura\",\"apellidoPaterno\":\"Martinez\",\"apellidoMaterno\":\"Lopez\",\"email\":\"laura@escuela.com\",\"telefono\":\"5512345678\"}";
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(put(RUTA + "/{id}",id).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void actualizar_emailDeOtroMaestro_lanzaConflictoException() throws Exception {
        // Arrange
        Long id = maestroRepository.saveAndFlush(Maestro.crear("Ana","Garcia","Ramos","libre@escuela.com","5598765432")).getId();
        String json = "{\"nombre\":\"Ana\",\"apellidoPaterno\":\"Garcia\",\"apellidoMaterno\":\"Ramos\",\"email\":\"laura@escuela.com\",\"telefono\":\"5598765432\"}";
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
    void eliminar_sinGrupos_eliminaMaestro() throws Exception {
        // Arrange
        Long id = maestroRepository.saveAndFlush(Maestro.crear("Ana","Garcia","Ramos","libre@escuela.com","5598765432")).getId();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(delete(RUTA + "/{id}",id));

        // Assert
        resultado.andExpect(status().isNoContent());
        assertThat(maestroRepository.existsById(id)).isFalse();
    }

    @Test
    void eliminar_conGrupos_lanzaEntidadRelacionadaException() throws Exception {
        // Arrange
        Long id = maestro.getId();
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
        return "{\"nombre\":\"Ana\",\"apellidoPaterno\":\"Garcia\",\"apellidoMaterno\":\"Ramos\",\"email\":\"ana@escuela.com\",\"telefono\":\"5598765432\"}";
    }

    private Long prepararEntidad() {
        return maestro.getId();
    }

    
}
