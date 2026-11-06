package com.ramon.escuela.mappers;

import com.ramon.escuela.dto.cursos.CursoRequest;
import com.ramon.escuela.dto.cursos.CursoResponse;
import com.ramon.escuela.dto.datos.DatosCurso;
import com.ramon.escuela.entities.Curso;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CursoMapper implements CommonMapper<CursoRequest, CursoResponse, Curso> {

    @Override
    public Curso requestAEntidad(CursoRequest request) {
        return request == null
                ? null
                : Curso.crear(
                        request.nombre(),
                        request.descripcion(),
                        request.creditos());
    }

    @Override
    public CursoResponse entidadAResponse(Curso entidad) {
        return entidad == null
                ? null
                : new CursoResponse(
                entidad.getId(),
                entidad.getNombreCursos(),
                entidad.getDescripcion(),
                entidad.getCreditos()
        );
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
