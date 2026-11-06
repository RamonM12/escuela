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
public class HorarioMapper implements CommonMapper<HorarioRequest, HorarioResponse, Horario> {

    private final GrupoMapper grupoMapper;

    @Override
    public Horario requestAEntidad(HorarioRequest request) {
        return null;
    }

    public Horario requestAEntidad(HorarioRequest resquest, Grupo grupo){
        if(resquest == null) return null;

        DiaSemana dia = DiaSemana.obtenerDiaSemanaPorDescripcion(resquest.dia());

        return Horario.crear(grupo, dia, resquest.horaInicio(), resquest.horaFin());
    }

    @Override
    public HorarioResponse entidadAResponse(Horario entidad) {
        if (entidad == null) return null;

        return new HorarioResponse(
                entidad.getId(),
                grupoMapper.entidadADatosGrupo(entidad.getGrupo()),
                entidad.obtenerHorarioTexto()
        );
    }
}

