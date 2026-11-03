package com.ramon.escuela.mappers;

import com.ramon.escuela.dto.cursos.CursoRequest;
import com.ramon.escuela.dto.datos.DatosCurso;
import com.ramon.escuela.entities.Curso;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CursoMapper implements CommonMapper<CursoRequest, CursoRequest, Curso> {

    @Override
    public Curso requestAEntidad(CursoRequest request) {
        return null;
    }

    @Override
    public CursoRequest entidadAResponse(Curso entidad) {
        return null;
    }

    public DatosCurso entidadADatosCurso(Curso entidad){

        return entidad == null
                ? null
                : new DatosCurso(
                        entidad.getNombreCursos(),
                        entidad.getDescripcion(),
                        entidad.getCreditos());
    }
}
