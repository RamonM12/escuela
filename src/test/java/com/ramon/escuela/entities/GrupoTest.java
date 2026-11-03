package com.ramon.escuela.entities;

import com.ramon.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
public class GrupoTest {
    @Test
    void crear_debeRetornarGrupo_cuandoDatosSonValidos() {
        // Arrange
        Curso curso = new Curso();
        Maestro maestro = new Maestro();
        Aula aula = new Aula();

        // Act
        Grupo grupo = Grupo.crear(curso, maestro, aula, "2026-1");

        // Assert
        assertThat(grupo).isNotNull();
        assertThat(grupo.getCurso()).isEqualTo(curso);
        assertThat(grupo.getMaestro()).isEqualTo(maestro);
        assertThat(grupo.getAula()).isEqualTo(aula);
        assertThat(grupo.getPeriodo()).isEqualTo("2026-1");
    }

    @Test
    void actualizar_debeModificarDatos_cuandoTodoEsValido() {
        // Arrange
        Curso curso = new Curso();
        Maestro maestro = new Maestro();
        Aula aula = new Aula();

        // Act
        Grupo grupo = Grupo.crear(curso, maestro, aula, "2026-1");

        // Assert
        assertThat(grupo).isNotNull();
        assertThat(grupo.getCurso()).isEqualTo(curso);
        assertThat(grupo.getMaestro()).isEqualTo(maestro);
        assertThat(grupo.getAula()).isEqualTo(aula);
        assertThat(grupo.getPeriodo()).isEqualTo("2026-1");
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoCursoEsNulo() {
        // Act + Assert
        assertThatThrownBy(() -> Grupo.crear(null, new Maestro(), new Aula(), "2026-1"))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("El curso es requerido");
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoPeriodoEsInvalido() {
        // Act + Assert
        assertThatThrownBy(() -> Grupo.crear(new Curso(), new Maestro(), new Aula(), "20"))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("El periodo es requerido y debe tener entre 3 y 20 caracteres");
    }
}
