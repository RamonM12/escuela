package com.ramon.escuela.entities;

import com.ramon.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AulaTest {

    @Test
    void crear_debeRetornarAula_cuandoDatosSonValidos() {
        // Act
        Aula aula = Aula.crear("Laboratorio de Sistemas", 30);

        // Assert
        assertThat(aula).isNotNull();
        assertThat(aula.getNombre()).isEqualTo("Laboratorio de Sistemas");
        assertThat(aula.getCapacidad()).isEqualTo(30);
    }

    @Test
    void actualizar_debeModificarDatos_cuandoTodoEsValido() {
        // Arrange
        Aula aula = Aula.builder()
                .nombre("Aula Vieja")
                .capacidad(20)
                .build();

        // Act
        aula.actualizar("Aula Magna", 50);

        // Assert
        assertThat(aula.getNombre()).isEqualTo("Aula Magna");
        assertThat(aula.getCapacidad()).isEqualTo(50);
    }

    @Test
    void actualizar_debeLanzarExcepcion_cuandoCapacidadEsInvalida() {
        // Arrange
        Aula aula = Aula.builder()
                .nombre("Laboratorio 1")
                .capacidad(30)
                .build();

        // Act + Assert
        assertThatThrownBy(() -> aula.actualizar("Laboratorio 1", 0))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("La capacidad debe ser un número entero positivo");
    }
}