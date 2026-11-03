package com.ramon.escuela.entities;

import com.ramon.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AlumnoTest {
    @Test
    void crear_debeRetornarAlumno_cuandoDatosSonValidos() {
        // Act
        Alumno alumno = Alumno.crear(
                "José Ramón",
                "Mora",
                "Cruz",
                "jose.mora@escuela.com",
                "20260001"
        );

        // Assert
        assertThat(alumno).isNotNull();
        assertThat(alumno.getNombreAlumno()).isEqualTo("José Ramón");
        assertThat(alumno.getApellidoPaternoAlumno()).isEqualTo("Mora");
        assertThat(alumno.getApellidoMaternoAlumno()).isEqualTo("Cruz");
        assertThat(alumno.getEmail()).isEqualTo("jose.mora@escuela.com");
        assertThat(alumno.getMatricula()).isEqualTo("20260001");
    }

    @Test
    void actualizar_debeModificarDatos_cuandoTodoEsValido() {
        // Arrange
        Alumno alumno = Alumno.builder()
                .nombreAlumno("Jose")
                .apellidoPaternoAlumno("Mora")
                .apellidoMaternoAlumno("Cruz")
                .email("jose@mail.com")
                .matricula("20260001")
                .build();

        // Act
        alumno.actualizar("José Ramón", "Mora", "Cruz", "jose.nuevo@mail.com", "20260001");

        // Assert
        assertThat(alumno.getNombreAlumno()).isEqualTo("José Ramón");
        assertThat(alumno.getEmail()).isEqualTo("jose.nuevo@mail.com");
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoNombreEsMuyCorto() {
        // Act + Assert
        assertThatThrownBy(() -> Alumno.crear(
                "J", // Muy corto (< 2)
                "Mora",
                "Cruz",
                "jose@mail.com",
                "20260001"
        ))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("El nombre es requerido y debe tener entre 2 y 50 caracteres");
    }
}
