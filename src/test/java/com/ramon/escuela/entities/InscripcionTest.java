package com.ramon.escuela.entities;

import com.ramon.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class InscripcionTest {

    @Test
    void crear_debeRetornarInscripcion_cuandoDatosSonValidos() {
        // Arrange
        Alumno alumno = new Alumno();
        Grupo grupo = new Grupo();

        // Act
        Inscripcion inscripcion = Inscripcion.crear(alumno, grupo);

        // Assert
        assertThat(inscripcion).isNotNull();
        assertThat(inscripcion.getAlumno()).isEqualTo(alumno);
        assertThat(inscripcion.getGrupo()).isEqualTo(grupo);
        assertThat(inscripcion.getFechaInscripcion()).isNotNull();
    }

    @Test
    void actualizar_debeModificarDatos_cuandoTodoEsValido() {
        // Arrange
        Inscripcion inscripcion = Inscripcion.builder()
                .alumno(new Alumno())
                .grupo(new Grupo())
                .build();

        Alumno nuevoAlumno = new Alumno();
        Grupo nuevoGrupo = new Grupo();

        // Act
        inscripcion.actualizar(nuevoAlumno, nuevoGrupo);

        // Assert
        assertThat(inscripcion.getAlumno()).isEqualTo(nuevoAlumno);
        assertThat(inscripcion.getGrupo()).isEqualTo(nuevoGrupo);
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoAlumnoEsNulo() {
        // Act + Assert
        assertThatThrownBy(() -> Inscripcion.crear(null, new Grupo()))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("El alumno es requerido");
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoGrupoEsNulo() {
        // Act + Assert
        assertThatThrownBy(() -> Inscripcion.crear(new Alumno(), null))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("El grupo es requerido");
    }
}
