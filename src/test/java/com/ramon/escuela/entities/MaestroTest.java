package com.ramon.escuela.entities;

import com.ramon.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class MaestroTest {
    @Test
    void crear_debeRetornarMaestro_cuandoDatosSonValidos() {
        // Act
        Maestro maestro = Maestro.crear(
                "Carlos",
                "Pérez",
                "Gómez",
                "carlos.perez@escuela.com",
                "4431234567"
        );

        // Assert
        assertThat(maestro).isNotNull();
        assertThat(maestro.getNombre()).isEqualTo("Carlos");
        assertThat(maestro.getApellidoPaterno()).isEqualTo("Pérez");
        assertThat(maestro.getApellidoMaterno()).isEqualTo("Gómez");
        assertThat(maestro.getEmail()).isEqualTo("carlos.perez@escuela.com");
        assertThat(maestro.getTelefono()).isEqualTo("4431234567");
    }

    @Test
    void actualizar_debeModificarDatos_cuandoTodoEsValido() {
        // Arrange
        Maestro maestro = Maestro.builder()
                .nombre("Carlos")
                .apellidoPaterno("Pérez")
                .apellidoMaterno("Gómez")
                .email("carlos@mail.com")
                .telefono("4431112233")
                .build();

        // Act
        maestro.actualizar("Carlos Alberto", "Pérez", "Hernández", "carlos.nuevo@mail.com", "4439998877");

        // Assert
        assertThat(maestro.getNombre()).isEqualTo("Carlos Alberto");
        assertThat(maestro.getApellidoMaterno()).isEqualTo("Hernández");
        assertThat(maestro.getEmail()).isEqualTo("carlos.nuevo@mail.com");
        assertThat(maestro.getTelefono()).isEqualTo("4439998877");
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoTelefonoNoTieneDiezCaracteres() {
        // Act + Assert
        assertThatThrownBy(() -> Maestro.crear(
                "Carlos",
                "Pérez",
                "Gómez",
                "carlos@mail.com",
                "12345" // Inválido (< 10)
        ))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("El telefono es requerido y debe tener entre 10 y 10 caracteres");
    }
}
