package com.ramon.escuela.dto.maestros;

import com.ramon.escuela.dto.datos.DatosCurso;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Informacion de una maestro")
public record MaestroResponse(
        @Schema(description = "ID del maestro", example = "1")
        Long id,

        @Schema(description = "Nombre del maestro", example = "Maximo decimo meridio")
        String nombre,

        @Schema(description = "Email del maestro", example = "test@test.com")
        String email,

        @Schema(description = "Telefono del maestro", example = "1234567890")
        String telefono,

        @Schema(description = "Datos del maestro")
        List<DatosCurso> cursos
) {
}
