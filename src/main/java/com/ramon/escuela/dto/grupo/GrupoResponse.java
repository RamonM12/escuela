package com.ramon.escuela.dto.grupo;

import com.ramon.escuela.dto.datos.DatosAula;
import com.ramon.escuela.dto.datos.DatosCurso;
import com.ramon.escuela.dto.datos.DatosMaestro;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "Informacion de los grupos")
public record GrupoResponse(
        @Schema(description = "ID del grupo", example = "1")
        Long id,

        @Schema(description = "Datos del curso")
        DatosCurso curso,

        @Schema(description = "Datos del maestro")
        DatosMaestro maestro,

        @Schema(description = "Nombre del aula", example = "Aula 101")
        DatosAula aula,

        @Schema(description = "Horarios del grupo", example = "[\"Lunes 08:00 - 10:00\"]")
        List<String> horarios,

        @Schema(description = "Periodo del grupo", example = "23/12/2026")
        String periodo
        ) {

}
