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
        Inscripcion inscripcion = new Inscripcion();
        Calificacion calificacion = Calificacion.builder()
                .calificacion(new BigDecimal("8.0"))
                .inscripcion(inscripcion)
                .build();

        // Act
        calificacion.actualizar(new BigDecimal("10.0"), inscripcion);

        // Assert
        assertThat(calificacion.getCalificacion()).isEqualByComparingTo("10.0");
    }

    @Test
    void actualizar_debeLanzarExcepcion_cuandoCalificacionEsNegativa() {
        // Arrange
        Inscripcion inscripcion = new Inscripcion();
        Calificacion calificacion = Calificacion.builder()
                .calificacion(new BigDecimal("8.0"))
                .inscripcion(inscripcion)
                .build();

        // Act + Assert
        assertThatThrownBy(() -> calificacion.actualizar(new BigDecimal("-1.5"), inscripcion))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("La calificación es requerida y debe estar entre 0 y 10");
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoCalificacionEsMayorADiez() {
        // Act + Assert
        assertThatThrownBy(() -> Calificacion.crear(new BigDecimal("10.5"), new Inscripcion()))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("La calificación es requerida y debe estar entre 0 y 10");
    }

    @Test
    void crear_debePermitirCero_porqueEstaDentroDelRango() {
        // Act
        Calificacion calificacion = Calificacion.crear(BigDecimal.ZERO, new Inscripcion());

        // Assert
        assertThat(calificacion.getCalificacion()).isEqualByComparingTo("0");
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoInscripcionEsNula() {
        // Act + Assert
        assertThatThrownBy(() -> Calificacion.crear(new BigDecimal("9.0"), null))
                .isInstanceOf(DatoInvalidoException.class)
                .hasMessage("La inscripción asociada es requerida");
    }
}