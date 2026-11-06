package com.ramon.escuela.dto.horario;

import com.ramon.escuela.dto.datos.DatosGrupo;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Informacion de los horarios")
public record HorarioResponse(
        @Schema(description = "ID del horario", example = "1")
        Long id,

        @Schema(description = "Datos del grupo")
        DatosGrupo grupo,

        @Schema(description = "Dia y horas del horario", example = "Lunes 08:00 - 10:00")
        String horario
) {
}