package com.ramon.escuela.services.Calificacion;

import com.ramon.escuela.dto.calificacion.CalificacionRequest;
import com.ramon.escuela.dto.calificacion.CalificacionResponse;
import com.ramon.escuela.entities.Calificacion;
import com.ramon.escuela.entities.Inscripcion;
import com.ramon.escuela.exceptions.ConflictException;
import com.ramon.escuela.exceptions.RecursoNoEncontradoException;
import com.ramon.escuela.mappers.CalificacionMapper;
import com.ramon.escuela.repositories.CalificacionRepository;
import com.ramon.escuela.repositories.InscripcionRepository;
import com.ramon.escuela.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CalificacionServiceImpl implements CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final CalificacionMapper calificacionMapper;
    private final InscripcionRepository inscripcionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CalificacionResponse> listar() {
        log.info("Listando todas las calificaciones...");

        return calificacionRepository.findAll().stream()
                .map(calificacionMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CalificacionResponse obtenerPorId(Long id) {
        Calificacion calificacion = calificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Calificacion no encontrada con ID: " + id));
        return calificacionMapper.entidadAResponse(calificacion);
    }

    @Override
    @Transactional
    public CalificacionResponse registrar(CalificacionRequest request) {
        log.info("Registrando nueva calificacion....");

        Inscripcion inscripcion = obtenerInscripcion(request.idInscripcion());

        if (calificacionRepository.existsByInscripcionId(inscripcion.getId()))
            throw new ConflictException("La inscripcion ya tiene una calificacion registrada");

        Calificacion calificacion = calificacionMapper.requestAEntidad(request, inscripcion);

        calificacionRepository.saveAndFlush(calificacion);

        log.info("Nueva calificacion {} registrada", calificacion.getId());

        return calificacionMapper.entidadAResponse(calificacion);
    }

    @Override
    @Transactional
    public CalificacionResponse actualizar(CalificacionRequest request, Long id) {
        Calificacion calificacion = obtenerCalificacion(id);

        Inscripcion inscripcion = obtenerInscripcion(request.idInscripcion());

        if (calificacionRepository.existsByInscripcionIdAndIdNot(inscripcion.getId(), id))
            throw new ConflictException("La inscripcion ya tiene otra calificacion registrada");

        if (calificacion.cambioEnDatos(request.calificacion(), inscripcion)) {
            log.info("Actualizando calificacion con id: {}", id);

            calificacion.actualizar(request.calificacion(), inscripcion);

            calificacionRepository.saveAndFlush(calificacion);

            log.info("Datos de la calificacion {} actualizados", id);
        }

        return calificacionMapper.entidadAResponse(calificacion);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Calificacion calificacion = obtenerCalificacion(id);

        log.info("Eliminando calificacion id: {}", id);

        calificacionRepository.delete(calificacion);
        calificacionRepository.flush();

        log.info("Calificacion {} eliminada", id);
    }

    private Calificacion obtenerCalificacion(Long id) {
        return ServiceUtils.obtenerEntidadException(calificacionRepository, id, Calificacion.class);
    }

    private Inscripcion obtenerInscripcion(Long id) {
        return ServiceUtils.obtenerEntidadException(inscripcionRepository, id, Inscripcion.class);
    }
}