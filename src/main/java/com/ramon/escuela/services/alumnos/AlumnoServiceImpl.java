package com.ramon.escuela.services.alumnos;

import com.ramon.escuela.dto.alumnos.AlumnoRequest;
import com.ramon.escuela.dto.alumnos.AlumnoResponse;
import com.ramon.escuela.entities.Alumno;
import com.ramon.escuela.exceptions.EntidadRelacionadaException;
import com.ramon.escuela.exceptions.RecursoNoEncontradoException;
import com.ramon.escuela.mappers.AlumnoMapper;
import com.ramon.escuela.repositories.AlumnoRepository;
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
public class AlumnoServiceImpl implements AlumnoService{

    private final AlumnoRepository alumnoRepository;
    private final AlumnoMapper alumnoMapper;
    private final InscripcionRepository inscripcionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AlumnoResponse> listar() {
        log.info("Listando todos los alumnos...");

        return alumnoRepository.findAll().stream()
                .map(alumnoMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AlumnoResponse obtenerPorId(Long id) {
        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(( )-> new RecursoNoEncontradoException("Alumno no encontrado con ID: " + id));
        return alumnoMapper.entidadAResponse(alumno);
    }

    @Override
    @Transactional
    public AlumnoResponse registrar(AlumnoRequest request) {
        log.info("Registrando nuevo alumno....");

        // 1. Creamos la entidad usando el método crear de 3 parámetros que ya tienes en la entidad
        Alumno alumno = alumnoMapper.requestAEntidad(
                request,
                generarEmail(request),
                generarMatricula(request)
        );

        alumnoRepository.saveAndFlush(alumno);

        log.info("Nuevo alumno {} registrado", alumno.obtenerNombreCompleto());

        return alumnoMapper.entidadAResponse(alumno);

    }

    @Override
    @Transactional
    public AlumnoResponse actualizar(AlumnoRequest request, Long id) {
       Alumno alumno = obtenerAlumno(id);

       if(alumno.cambioEnDatos(
               request.nombre(),
               request.apellidoPaterno(),
               request.apellidoMaterno()
       )){
           log.info("Actualizando alumno con id: {}", id);

           alumno.actualizar(
                   request.nombre(),
                   request.apellidoPaterno(),
                   request.apellidoMaterno(),
                   generarEmail(request),
                   generarMatricula(request));

           alumnoRepository.saveAndFlush(alumno);

           log.info("Datos del alumno {} actualizados", alumno.obtenerNombreCompleto());
        }

        return alumnoMapper.entidadAResponse(alumno);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Alumno alumno = obtenerAlumno(id);

        log.info("Eliminando alumno id: {}", id );

        if(inscripcionRepository.existsByAlumnoId(id))
            throw new EntidadRelacionadaException(
                    "No se puede eliminar el alumno ya que " +
                            "tiene inscripsiones asignadas"
            );

        alumnoRepository.delete(alumno);
        alumnoRepository.flush();

        log.info("Alumno {} eliminado", alumno.obtenerNombreCompleto());

    }

    private Alumno obtenerAlumno(Long id){
        return ServiceUtils.obtenerEntidadException(alumnoRepository, id, Alumno.class);
    }

    private String generarEmail(AlumnoRequest request){
        log.info("Generando email....");

        return alumnoRepository.generarEmail(
                request.nombre().trim(),
                request.apellidoPaterno().trim(),
                request.apellidoMaterno().trim());
    }

    private String generarMatricula(AlumnoRequest request){
        log.info("Generando matricula....");

        return alumnoRepository.generarMatricula(
                request.nombre().trim(),
                request.apellidoPaterno().trim(),
                request.apellidoMaterno().trim());
    }


}
