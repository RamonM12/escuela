package com.ramon.escuela.dto.cursos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informacion del los cursos")
public record CursoResponse(
        @Schema(description = "ID del curso", example = "1")
        Long id,

        @Schema(description = "Nombre del curso", example = "Matematicas")
        String nombre,

        @Schema(description = "Descripcion del curso", example = "Matematicas II")
        String descripcion,

        @Schema(description = "Creditos del curso", example = "10")
        Integer creditos
) {
}
