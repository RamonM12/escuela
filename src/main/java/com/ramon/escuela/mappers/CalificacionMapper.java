package com.ramon.escuela.mappers;

import com.ramon.escuela.dto.calificacion.CalificacionRequest;
import com.ramon.escuela.dto.calificacion.CalificacionResponse;
import com.ramon.escuela.entities.Calificacion;
import com.ramon.escuela.entities.Inscripcion;
import com.ramon.escuela.utils.StringCustomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CalificacionMapper {

    private final InscripcionMapper inscripcionMapper;

    public Calificacion requestAEntidad(CalificacionRequest request, Inscripcion inscripcion) {
        if (request == null) return null;

        return Calificacion.crear(request.calificacion(), inscripcion);
    }

    public CalificacionResponse entidadAResponse(Calificacion entidad) {
        if (entidad == null) return null;

        return new CalificacionResponse(
                entidad.getId(),
                inscripcionMapper.entidadADatosInscripcion(entidad.getInscripcion()),
                entidad.getCalificacion(),
                StringCustomUtils.localDateAString(entidad.getFechaRegistro())
        );
    }
}