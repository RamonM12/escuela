package com.ramon.escuela.dto.datos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de una inscripcion")
public record DatosInscripcion(
        @Schema(description = "Datos del alumno")
        DatosAlumno alumno,

        @Schema(description = "Datos del grupo")
        DatosGrupo grupo,

        @Schema(description = "Fecha de inscripcion", example = "11/02/2026")
        String fechaInscripcion
) {
}