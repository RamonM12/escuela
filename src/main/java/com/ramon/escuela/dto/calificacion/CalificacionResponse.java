package com.ramon.escuela.dto.calificacion;

import com.ramon.escuela.dto.datos.DatosInscripcion;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Informacion de las calificaciones")
public record CalificacionResponse(
        @Schema(description = "ID de la calificacion", example = "1")
        Long id,

        @Schema(description = "Datos de la inscripcion")
        DatosInscripcion inscripcion,

        @Schema(description = "Calificacion", example = "9")
        BigDecimal calificacion,

        @Schema(description = "Fecha de registro", example = "11/02/2026")
        String fechaRegistro
) {
}