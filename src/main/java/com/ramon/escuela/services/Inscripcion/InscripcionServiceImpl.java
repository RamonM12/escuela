package com.ramon.escuela.services.Inscripcion;

import com.ramon.escuela.dto.inscripcion.InscripcionRequest;
import com.ramon.escuela.dto.inscripcion.InscripcionResponse;
import com.ramon.escuela.entities.Alumno;
import com.ramon.escuela.entities.Grupo;
import com.ramon.escuela.entities.Inscripcion;
import com.ramon.escuela.exceptions.ConflictException;
import com.ramon.escuela.exceptions.EntidadRelacionadaException;
import com.ramon.escuela.mappers.InscripcionMapper;
import com.ramon.escuela.repositories.AlumnoRepository;
import com.ramon.escuela.repositories.GrupoRepository;
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
public class InscripcionServiceImpl implements InscripcionService {
    private final InscripcionRepository inscripcionRepository;
    private final InscripcionMapper inscripcionMapper;
    private final AlumnoRepository alumnoRepository;
    private final GrupoRepository grupoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<InscripcionResponse> listar() {
        log.info("Listando todas las inscripciones...");

        return inscripcionRepository.findAll().stream()
                .map(inscripcionMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InscripcionResponse obtenerPorId(Long id) {
        return inscripcionMapper.entidadAResponse(obtenerInscripcion(id));
    }

    @Override
    @Transactional
    public InscripcionResponse registrar(InscripcionRequest request) {
        log.info("Registrando nueva inscripcion....");

        Alumno alumno = obtenerAlumno(request.idAlumno());
        Grupo grupo = obtenerGrupo(request.idGrupo());

        if (inscripcionRepository.existsByAlumnoIdAndGrupoId(alumno.getId(), grupo.getId()))
            throw new ConflictException("El alumno ya esta inscrito en ese grupo");

        Inscripcion inscripcion = inscripcionMapper.requestAEntidad(request, alumno, grupo);

        inscripcionRepository.saveAndFlush(inscripcion);

        log.info("Nueva inscripcion {} registrada", inscripcion.getId());

        return inscripcionMapper.entidadAResponse(inscripcion);
    }

    @Override
    @Transactional
    public InscripcionResponse actualizar(InscripcionRequest request, Long id) {
        Inscripcion inscripcion = obtenerInscripcion(id);

        Alumno alumno = obtenerAlumno(request.idAlumno());
        Grupo grupo = obtenerGrupo(request.idGrupo());

        if (inscripcionRepository.existsByAlumnoIdAndGrupoIdAndIdNot(alumno.getId(), grupo.getId(), id))
            throw new ConflictException("El alumno ya esta inscrito en ese grupo");

        if (inscripcion.cambioEnDatos(alumno, grupo)) {
            log.info("Actualizando inscripcion con id: {}", id);

            inscripcion.actualizar(alumno, grupo);

            inscripcionRepository.saveAndFlush(inscripcion);

            log.info("Datos de la inscripcion {} actualizados", id);
        }

        return inscripcionMapper.entidadAResponse(inscripcion);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Inscripcion inscripcion = obtenerInscripcion(id);

        log.info("Eliminando inscripcion id: {}", id);

        if (inscripcion.getCalificacion() != null)
            throw new EntidadRelacionadaException(
                    "No se puede eliminar la inscripcion ya que " +
                            "tiene una calificacion asociada"
            );

        inscripcionRepository.delete(inscripcion);
        inscripcionRepository.flush();

        log.info("Inscripcion {} eliminada", id);
    }

    private Inscripcion obtenerInscripcion(Long id) {
        return ServiceUtils.obtenerEntidadException(inscripcionRepository, id, Inscripcion.class);
    }

    private Alumno obtenerAlumno(Long id) {
        return ServiceUtils.obtenerEntidadException(alumnoRepository, id, Alumno.class);
    }

    private Grupo obtenerGrupo(Long id) {
        return ServiceUtils.obtenerEntidadException(grupoRepository, id, Grupo.class);
    }
}