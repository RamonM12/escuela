package com.ramon.escuela.controllers;

import com.ramon.escuela.entities.*;
import com.ramon.escuela.repositories.*;
import com.ramon.escuela.services.horario.HorarioService;
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
        "spring.datasource.url=jdbc:h2:mem:pruebasHorario;MODE=Oracle;DB_CLOSE_DELAY=-1",
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
class HorarioControllerIT {
    private static final String RUTA = "/api/horarios";
    @Autowired MockMvc mvc;
    @Autowired EntityManager entityManager;
    @MockitoSpyBean HorarioService service;
    @Autowired MaestroRepository maestroRepository;
    @Autowired AulaRepository aulaRepository;
    @Autowired CursoRepository cursoRepository;
    @Autowired GrupoRepository grupoRepository;
    @Autowired HorarioRepository horarioRepository;
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
    void registrar_datosValidos_registraHorario() throws Exception {
        // Arrange
        String json = requestValido();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(post(RUTA).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNumber()).andExpect(jsonPath("$.horario").value("Lunes 08:00 - 10:00"));
    }

    @Test
    void registrar_mismoGrupoDatosIgualesExceptoPeriodo_registraExitosamente() throws Exception {
        // Arrange
        guardarHorario(grupo,"08:00","10:00");
        Grupo siguiente = grupoOtroPeriodo();
        String json = "{\"idGrupo\":%d,\"dia\":\"Lunes\",\"horaInicio\":\"08:00\",\"horaFin\":\"10:00\"}".formatted(siguiente.getId());
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(post(RUTA).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().isCreated()).andExpect(jsonPath("$.grupo.periodo").value("2026-02"));
    }

    @Test
    void registrar_horaInicioMayorAHoraFin_lanzaDatoInvalidoException() throws Exception {
        // Arrange
        String json = "{\"idGrupo\":%d,\"dia\":\"Lunes\",\"horaInicio\":\"10:00\",\"horaFin\":\"08:00\"}".formatted(grupo.getId());
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(post(RUTA).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().is(400))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void registrar_horaInicioIgualAHoraFin_lanzaDatoInvalidoException() throws Exception {
        // Arrange
        String json = "{\"idGrupo\":%d,\"dia\":\"Lunes\",\"horaInicio\":\"08:00\",\"horaFin\":\"08:00\"}".formatted(grupo.getId());
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(post(RUTA).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().is(400))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void registrar_aulaOcupadaPorOtroGrupo_lanzaConflictoException() throws Exception {
        // Arrange
        guardarHorario(grupo,"08:00","10:00");
        Grupo otro = otroGrupoMismoPeriodo();
        String json = "{\"idGrupo\":%d,\"dia\":\"Lunes\",\"horaInicio\":\"09:00\",\"horaFin\":\"11:00\"}".formatted(otro.getId());
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
    void registrar_traslapeEnElMismoGrupo_lanzaConflictoException() throws Exception {
        // Arrange
        guardarHorario(grupo,"08:00","10:00");
        String json = "{\"idGrupo\":%d,\"dia\":\"Lunes\",\"horaInicio\":\"09:00\",\"horaFin\":\"11:00\"}".formatted(grupo.getId());
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
    void registrar_formatoHoraInvalido_retorna400() throws Exception {
        // Arrange
        String json = "{\"idGrupo\":%d,\"dia\":\"Lunes\",\"horaInicio\":\"8:00\",\"horaFin\":\"10:00\"}".formatted(grupo.getId());
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
    void registrar_grupoInexistente_lanzaRecursoNoEncontradoException() throws Exception {
        // Arrange
        String json = "{\"idGrupo\":%d,\"dia\":\"Lunes\",\"horaInicio\":\"08:00\",\"horaFin\":\"10:00\"}".formatted(999999L);
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
    void obtenerPorId_horarioExistente_retornaHorario() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(get(RUTA + "/{id}",id));

        // Assert
        resultado.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id)).andExpect(jsonPath("$.horario").value("Lunes 08:00 - 10:00"));
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
    void listar_conHorarios_retornaLista() throws Exception {
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
    void actualizar_datosValidos_actualizaHorario() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        String json = requestValido();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(put(RUTA + "/{id}",id).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void actualizar_mismoGrupoDatosIgualesExceptoPeriodo_actualizaExitosamente() throws Exception {
        // Arrange
        guardarHorario(grupo,"08:00","10:00");
        Grupo siguiente = grupoOtroPeriodo();
        Long id = guardarHorario(siguiente,"12:00","13:00").getId();
        String json = "{\"idGrupo\":%d,\"dia\":\"Lunes\",\"horaInicio\":\"08:00\",\"horaFin\":\"10:00\"}".formatted(siguiente.getId());
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(put(RUTA + "/{id}",id).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().isOk()).andExpect(jsonPath("$.grupo.periodo").value("2026-02"));
    }

    @Test
    void actualizar_horaInicioMayorAHoraFin_lanzaDatoInvalidoException() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        String json = "{\"idGrupo\":%d,\"dia\":\"Lunes\",\"horaInicio\":\"10:00\",\"horaFin\":\"08:00\"}".formatted(grupo.getId());
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(put(RUTA + "/{id}",id).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().is(400))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void actualizar_aulaOcupadaPorOtroGrupo_lanzaConflictoException() throws Exception {
        // Arrange
        guardarHorario(grupo,"08:00","10:00");
        Grupo otro = otroGrupoMismoPeriodo();
        Long id = guardarHorario(otro,"12:00","13:00").getId();
        String json = "{\"idGrupo\":%d,\"dia\":\"Lunes\",\"horaInicio\":\"09:00\",\"horaFin\":\"11:00\"}".formatted(otro.getId());
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
    void eliminar_horarioExistente_eliminaHorario() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        entityManager.flush();
        entityManager.clear();

        // Act
        ResultActions resultado = mvc.perform(delete(RUTA + "/{id}",id));

        // Assert
        resultado.andExpect(status().isNoContent());
        assertThat(horarioRepository.existsById(id)).isFalse();
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
        return "{\"idGrupo\":%d,\"dia\":\"Lunes\",\"horaInicio\":\"08:00\",\"horaFin\":\"10:00\"}".formatted(grupo.getId());
    }

    private Long prepararEntidad() {
        return guardarHorario(grupo,"08:00","10:00").getId();
    }

    private Horario guardarHorario(Grupo destino,String inicio,String fin) {
        Horario horario = Horario.crear(destino,com.ramon.escuela.enums.DiaSemana.LUNES,inicio,fin);
        return horarioRepository.saveAndFlush(horario);
    }
    
    private Grupo grupoOtroPeriodo() {
        Grupo otro = Grupo.crear(curso,maestro,aula,"2026-02");
        return grupoRepository.saveAndFlush(otro);
    }
    
    private Grupo otroGrupoMismoPeriodo() {
        Curso otroCurso = cursoRepository.saveAndFlush(Curso.crear("Fisica",null,6));
        Grupo otro = Grupo.crear(otroCurso,maestro,aula,"2026-01");
        return grupoRepository.saveAndFlush(otro);
    }
}
