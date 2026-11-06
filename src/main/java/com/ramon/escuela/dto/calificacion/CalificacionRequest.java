package com.ramon.escuela.dto.calificacion;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Datos necesarios para registrar o actualizar una calificacion")
public record CalificacionRequest(
        @Schema(description = "ID de la inscripcion", example = "1")
        @NotNull(message = "La inscripcion es requerida")
        @Positive(message = "El ID de la inscripcion debe ser positivo")
        Long idInscripcion,

        @Schema(description = "Calificacion de 0 a 10 con un decimal", example = "8.5")
        @NotNull(message = "La calificacion es requerida")
        @DecimalMin(value = "0.0", message = "La calificacion no puede ser menor a 0")
        @DecimalMax(value = "10.0", message = "La calificacion no puede ser mayor a 10")
        @Digits(integer = 2, fraction = 1, message = "La calificacion solo puede tener un decimal")
        BigDecimal calificacion
) {
}