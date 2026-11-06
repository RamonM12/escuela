package com.ramon.escuela.mappers;

import com.ramon.escuela.dto.datos.DatosGrupo;
import com.ramon.escuela.dto.grupo.GrupoResponse;
import com.ramon.escuela.dto.grupo.GrupoResquest;
import com.ramon.escuela.entities.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GrupoMapper {

    private final CursoMapper cursoMapper;
    private final MaestroMapper maestroMapper;
    private final AulaMapper aulaMapper;

    public Grupo requestAEntidad(GrupoResquest request, Curso curso, Maestro maestro, Aula aula){
        return request == null
                ? null
                : Grupo.crear(
                curso,
                maestro,
                aula,
                request.periodo()
        );
    }

    public GrupoResponse entidadAResponse(Grupo entidad) {
        if (entidad == null) return null;

        List<String> horarios = entidad.getHorarios().stream()
                .map(Horario::obtenerHorarioTexto)
                .toList();

        return new GrupoResponse(
                entidad.getId(),
                cursoMapper.entidadADatosCurso(entidad.getCurso()),
                maestroMapper.entidadADatosMaestro(entidad.getMaestro()),
                aulaMapper.entidadADatosAula(entidad.getAula()),
                horarios,
                entidad.getPeriodo()
        );
    }

    public DatosGrupo entidadADatosGrupo(Grupo entidad) {
        return entidad == null
                ? null
                : new DatosGrupo(
                entidad.getCurso().getNombreCursos(),
                entidad.getMaestro().obtenerNombreCompleto(),
                entidad.getAula().getNombre(),
                entidad.getPeriodo());
    }
}