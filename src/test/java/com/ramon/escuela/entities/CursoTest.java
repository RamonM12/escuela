package com.ramon.escuela.entities;

import com.ramon.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CursoTest {
    @Test
    void crear_debeRetornarCurso_cuandoDatosSonValidos() {
        // Act
        Curso curso = Curso.crear("Programación Avanzada", "Curso de backend con Spring Boot", 8);

        // Assert
        assertThat(curso).isNotNull();
        assertThat(curso.getNombreCursos()).isEqualTo("Programación Avanzada");
        assertThat(curso.getDescripcion()).isEqualTo("Curso de backend con Spring Boot");
        assertThat(curso.getCreditos()).isEqualTo(8);
    }

    @Test
    void actualizar_debeModificarDatos_cuandoTodoEsValido() {
        // Arrange
        Curso curso = Curso.builder()
                .nombreCursos("Matemáticas")
                .descripcion("Básicas")
                .creditos(5)
                .build();

        // Act
        curso.actualizar("Matemáticas Avanzadas", "Cálculo integral y diferencial", 10);

        // Assert
        assertThat(curso.getNombreCursos()).isEqualTo("Matemáticas Avanzadas");
        assertThat(curso.getDescripcion()).isEqualTo("Cálculo integral y diferencial");
        assertThat(curso.getCreditos()).isEqualTo(10);
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoNombreEsMuyCorto() {
        // Act + Assert
        assertThatThrownBy(() -> Curso.crear("Pr", "Descripción válida", 5))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("El nombre del curso es requerido y debe tener entre 3 y 100 caracteres");
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoCreditosSonInvalidos() {
        // Act + Assert
        assertThatThrownBy(() -> Curso.crear("Base de Datos", "SQL y NoSQL", 0))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("Los créditos son requeridos y deben ser un número entero positivo");
    }
}
