package com.ramon.escuela.dto.maestros;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos necesarios para registrar o actualizar un maestro")
public record MaestroRequest(
        @Schema(description = "Nomobre del maestro", example = "Maximo")
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1, max = 50,message = "El nombre debe tener entre 1 y 50 caracteres")
        String nombre,

        @Schema(description = "Apellido paterno del maestro", example = "Maximo")
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1, max = 50,message = "El nombre debe tener entre 1 y 50 caracteres")
        String apellidoPaterno,

        @Schema(description = "Apellido materno del maestro", example = "Maximo")
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1, max = 50,message = "El nombre debe tener entre 1 y 50 caracteres")
        String apellidoMaterno,

        @Schema(description = "El email del maestro", example = "test@test.com")
        @NotBlank(message = "El email es requerido")
        @Size(min = 1, max = 50,message = "El email debe tener entre 8 y 100 caracteres")
        @Email(message = "El emai debe tener un formato valido(ejemplo: test.test@gmail.com)")
        String email,

        @Schema(description = "Telefono del maestro", example = "5656846565")
        @NotBlank(message = "El telefono es requerido")
        @Pattern(regexp = "^[0-9]{10}",message = "El telefono debe tener 10 caracteres")
        String telefono
) {

}
