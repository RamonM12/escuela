package com.ramon.escuela.dto.alumnos;

import com.ramon.escuela.dto.datos.DatosCalificacion;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "Informacion de un alumno")
public record AlumnoResponse(

        @Schema(description = "ID del alumno", example = "1")
        Long id,

        @Schema(description = "Nombre del alumno", example = "Maximo")
        String nombre,

        @Schema(description = "Email del alumno", example = "test@test.com")
        String email,

        @Schema(description = "Matricula del alumno", example = "163264564")
        String matricula,

        @Schema(description = "Fecha de ingreso del alumno", example = "11/05/2026")
        String fechaIngreso,

        @Schema(description = "Datos de las calificaciones del alumno")
        List<DatosCalificacion> calificaciones,

        @Schema(description = "Promedio del alumno", example = "9.9")
        BigDecimal promedio
) {
}
