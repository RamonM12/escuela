package com.ramon.escuela.entities;

import com.ramon.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CalificacionTest {
    @Test
    void crear_debeRetornarCalificacion_cuandoDatosSonValidos() {
        // Arrange
        Inscripcion inscripcion = new Inscripcion();

        // Act
        Calificacion calificacion = Calificacion.crear(new BigDecimal("9.5"), inscripcion);

        // Assert
        assertThat(calificacion).isNotNull();
        assertThat(calificacion.getCalificacion()).isEqualByComparingTo("9.5");
        assertThat(calificacion.getInscripcion()).isEqualTo(inscripcion);
        assertThat(calificacion.getFechaRegistro()).isNotNull();
    }

    @Test
    void actualizar_debeModificarCalificacion_cuandoEsValida() {
        // Arrange
        Calificacion calificacion = Calificacion.builder()
                .calificacion(new BigDecimal("8.0"))
                .build();

        // Act
        calificacion.actualizar(new BigDecimal("10.0"));

        // Assert
        assertThat(calificacion.getCalificacion()).isEqualByComparingTo("10.0");
    }

    @Test
    void actualizar_debeLanzarExcepcion_cuandoCalificacionEsNegativa() {
        // Arrange
        Calificacion calificacion = Calificacion.builder()
                .calificacion(new BigDecimal("8.0"))
                .build();

        // Act + Assert
        assertThatThrownBy(() -> calificacion.actualizar(new BigDecimal("-1.5")))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("La calificación es requerida y debe ser un valor positivo");
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoInscripcionEsNula() {
        // Act + Assert
        assertThatThrownBy(() -> Calificacion.crear(new BigDecimal("9.0"), null))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("La inscripción asociada es requerida");
    }
}
