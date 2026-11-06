package com.ramon.escuela.mappers;

import com.ramon.escuela.dto.aula.AulaResponse;
import com.ramon.escuela.dto.aula.AulaRequest;
import com.ramon.escuela.dto.datos.DatosAula;
import com.ramon.escuela.entities.Aula;
import org.springframework.stereotype.Component;

@Component
public class AulaMapper implements  CommonMapper<AulaRequest, AulaResponse, Aula> {
    @Override
    public Aula requestAEntidad(AulaRequest request) {
        return request == null
                ? null
                : Aula.crear(
                        request.nombre(),
                        request.capacidad());
    }

    @Override
    public AulaResponse entidadAResponse(Aula entidad) {
        return entidad == null
                ? null
                : new AulaResponse(
                        entidad.getId(),
                        entidad.getNombre(),
                        entidad.getCapacidad());
    }

    public DatosAula entidadADatosAula(Aula entidad) {
        return entidad == null
                ? null
                : new DatosAula(
                entidad.getNombre(),
                entidad.getCapacidad());
    }
}
