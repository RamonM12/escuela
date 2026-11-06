package com.ramon.escuela.dto.aula;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informacion de las aulas")
public record AulaResponse(
        @Schema(description = "ID del aula", example = "1")
        Long id,

        @Schema(description = "Nombre del aula", example = "Aula A1")
        String nombre,

        @Schema(description = "Capacidad de alumnos del aula", example = "30")
        Integer capacidad
) {
}
