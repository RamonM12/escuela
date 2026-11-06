package com.ramon.escuela.dto.aula;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos necesarios para registrar o actualizar a una aula")
public record AulaRequest(
        @Schema(description = "Nombre del aula", example = "Aula A1")
        @Size(min = 3, max = 30, message = "El nombre debe tener 3 a 30 caracteres")
        @NotBlank(message = "El nombre es requerido")
        String nombre,

        @Schema(description = "La capacidad de alumnos del aula", example = "30")
        @NotNull(message = "La capacidad es requerida")
        @Positive(message = "La capacidad debe ser mayor a 0")
        @Max(value = 999, message = "La capacidad no puede ser mayor a 999")
        Integer capacidad
) {
}
