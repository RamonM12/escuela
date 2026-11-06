package com.ramon.escuela.services.curso;

import com.ramon.escuela.dto.cursos.CursoRequest;
import com.ramon.escuela.dto.cursos.CursoResponse;
import com.ramon.escuela.entities.Curso;
import com.ramon.escuela.exceptions.ConflictException;
import com.ramon.escuela.exceptions.EntidadRelacionadaException;
import com.ramon.escuela.mappers.CursoMapper;
import com.ramon.escuela.repositories.CursoRepository;
import com.ramon.escuela.repositories.GrupoRepository;
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
public class CursoServiceImpl implements CursoService {

    private final CursoRepository cursoRepository;
    private final CursoMapper cursoMapper;
    private final GrupoRepository grupoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponse> listar() {
        log.info("Listando todos los cursos...");

        return cursoRepository.findAll().stream()
                .map(cursoMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CursoResponse obtenerPorId(Long id) {
        return cursoMapper.entidadAResponse(obtenerCurso(id));
    }

    @Override
    @Transactional
    public CursoResponse registrar(CursoRequest request) {
        log.info("Registrando nuevo curso....");
        if(cursoRepository.existsByNombreCursos(request.nombre().trim()))
            throw  new ConflictException("Ya existe un curso con el nombre " + request.nombre());

        Curso curso = cursoMapper.requestAEntidad(request);

        cursoRepository.saveAndFlush(curso);

        log.info("Nuevo curso {} registrado", curso.getNombreCursos());

        return cursoMapper.entidadAResponse(curso);
    }

    @Override
    @Transactional
    public CursoResponse actualizar(CursoRequest request, Long id) {
        Curso curso = obtenerCurso(id);

        if(cursoRepository.existsByNombreCursosAndIdNot(request.nombre().trim(),id))
            throw new ConflictException("Ya existe otro curso con el nombre " + request.nombre());

        if (curso.cambioEnDatos(
                request.nombre(),
                request.descripcion(),
                request.creditos()
        )) {
            log.info("Actualizando curso con id: {}", id);

            curso.actualizar(
                    request.nombre(),
                    request.descripcion(),
                    request.creditos());

            cursoRepository.saveAndFlush(curso);

            log.info("Datos del curso {} actualizados", curso.getNombreCursos());
        }

        return cursoMapper.entidadAResponse(curso);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Curso curso = obtenerCurso(id);

        log.info("Eliminando curso id: {}", id);

        if (grupoRepository.existsByCursoId(id))
            throw new EntidadRelacionadaException(
                    "No se puede eliminar el curso ya que " +
                            "tiene grupos asignados"
            );

        cursoRepository.delete(curso);
        cursoRepository.flush();

        log.info("Curso {} eliminado", curso.getNombreCursos());
    }

    private Curso obtenerCurso(Long id) {
        return ServiceUtils.obtenerEntidadException(cursoRepository, id, Curso.class);
    }
}