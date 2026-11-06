package com.ramon.escuela.mappers;

import com.ramon.escuela.dto.horario.HorarioResponse;
import com.ramon.escuela.dto.horario.HorarioRequest;
import com.ramon.escuela.entities.Grupo;
import com.ramon.escuela.entities.Horario;
import com.ramon.escuela.enums.DiaSemana;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HorarioMapper {

    private final GrupoMapper grupoMapper;

    public Horario requestAEntidad(HorarioRequest request, Grupo grupo){
        if(request == null) return null;

        DiaSemana dia = DiaSemana.obtenerDiaSemanaPorDescripcion(request.dia());

        return Horario.crear(grupo, dia, request.horaInicio(), request.horaFin());
    }

    public HorarioResponse entidadAResponse(Horario entidad) {
        if (entidad == null) return null;

        return new HorarioResponse(
                entidad.getId(),
                grupoMapper.entidadADatosGrupo(entidad.getGrupo()),
                entidad.obtenerHorarioTexto()
        );
    }
}