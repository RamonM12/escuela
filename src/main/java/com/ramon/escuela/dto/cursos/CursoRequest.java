package com.ramon.escuela.dto.cursos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Datos necesarios para registrar o actualizar a un curso")
public record CursoRequest(
        @Schema(description = "Nombre del curso", example = "Matematicas")
        @Size(min = 3, max = 100, message = "El nombre debe tener de 3 a 100 caracteres")
        @NotBlank(message = "El nombre es requerido")
        String nombre,

        @Schema(description = "Descripcion del curso", example = "El arte de las matematicas")
        @Size(max = 200, message = "La descripcion no puede superar los 200 caracteres")
        String descripcion,

        @Schema(description = "El numero de creditos del curso", example = "6")
        @NotNull(message = "Los creditos son requeridos")
        @Positive(message = "Los creditos deben ser mayores a 0")
        @Max(value = 99, message = "Los creditos no pueden ser mayores a 99")
        Integer creditos
) {
}
