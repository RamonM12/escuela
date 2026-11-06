package com.ramon.escuela.dto.inscripcion;

import com.ramon.escuela.dto.datos.DatosAlumno;
import com.ramon.escuela.dto.datos.DatosGrupo;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Informacion de las inscripciones")
public record InscripcionResponse(
        @Schema(description = "ID de la inscripcion", example = "1")
        Long id,

        @Schema(description = "Datos del alumno")
        DatosAlumno alumno,

        @Schema(description = "Datos del grupo")
        DatosGrupo grupo,

        @Schema(description = "Calificacion del alumno, null si aun no tiene", example = "9.5")
        BigDecimal calificacion,

        @Schema(description = "Fecha de inscripcion", example = "11/02/2026")
        String fechaInscripcion
) {
}