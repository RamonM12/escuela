package com.ramon.escuela.mappers;

import com.ramon.escuela.dto.datos.DatosInscripcion;
import com.ramon.escuela.dto.inscripcion.InscripcionRequest;
import com.ramon.escuela.dto.inscripcion.InscripcionResponse;
import com.ramon.escuela.entities.Alumno;
import com.ramon.escuela.entities.Grupo;
import com.ramon.escuela.entities.Inscripcion;
import com.ramon.escuela.utils.StringCustomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class InscripcionMapper {

    private final AlumnoMapper alumnoMapper;
    private final GrupoMapper grupoMapper;

    public Inscripcion requestAEntidad(InscripcionRequest request, Alumno alumno, Grupo grupo) {
        if (request == null) return null;

        return Inscripcion.crear(alumno, grupo);
    }

    public InscripcionResponse entidadAResponse(Inscripcion entidad) {
        if (entidad == null) return null;

        BigDecimal calificacion = entidad.getCalificacion() != null
                ? entidad.getCalificacion().getCalificacion()
                : null;

        return new InscripcionResponse(
                entidad.getId(),
                alumnoMapper.entidadADatosAlumno(entidad.getAlumno()),
                grupoMapper.entidadADatosGrupo(entidad.getGrupo()),
                calificacion,
                StringCustomUtils.localDateAString(entidad.getFechaInscripcion())
        );
    }

    public DatosInscripcion entidadADatosInscripcion(Inscripcion entidad) {
        return entidad == null
                ? null
                : new DatosInscripcion(
                alumnoMapper.entidadADatosAlumno(entidad.getAlumno()),
                grupoMapper.entidadADatosGrupo(entidad.getGrupo()),
                StringCustomUtils.localDateAString(entidad.getFechaInscripcion()));
    }
}