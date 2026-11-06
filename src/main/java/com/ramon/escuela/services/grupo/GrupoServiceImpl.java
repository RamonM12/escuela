package com.ramon.escuela.services.grupo;

import com.ramon.escuela.dto.grupo.GrupoResponse;
import com.ramon.escuela.dto.grupo.GrupoResquest;
import com.ramon.escuela.entities.Aula;
import com.ramon.escuela.entities.Curso;
import com.ramon.escuela.entities.Grupo;
import com.ramon.escuela.entities.Maestro;
import com.ramon.escuela.exceptions.ConflictException;
import com.ramon.escuela.exceptions.EntidadRelacionadaException;
import com.ramon.escuela.exceptions.RecursoNoEncontradoException;
import com.ramon.escuela.mappers.GrupoMapper;
import com.ramon.escuela.repositories.*;
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
public class GrupoServiceImpl implements GrupoService{

    private final GrupoRepository grupoRepository;
    private final GrupoMapper grupoMapper;
    private final CursoRepository cursoRepository;
    private final MaestroRepository maestroRepository;
    private final AulaRepository aulaRepository;
    private final InscripcionRepository inscripcionRepository;
    private final HorarioRepository horarioRepository;

    @Override
    public List<GrupoResponse> listar() {
        log.info("Listando todos los grupos..");

        return grupoRepository.findAll().stream()
                .map(grupoMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GrupoResponse obtenerPorId(Long id) {
        Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Grupo no encontrado con ID: " + id));

        return grupoMapper.entidadAResponse(grupo);
    }

    @Override
    @Transactional
    public GrupoResponse registrar(GrupoResquest request) {
        log.info("Resgistrando nuevo grupo");

        Curso curso = obtenerCurso(request.idCurso());
        Maestro maestro = obtenerMaestro(request.idMaestro());
        Aula aula = obtenerAula(request.idAula());

        if(grupoRepository.existsByCursoIdAndMaestroIdAndAulaIdAndPeriodo(
                curso.getId(),maestro.getId(),aula.getId(),request.periodo().trim()))
            throw new ConflictException("Ya existe un grupo con ese curso, maestro, aula y periodo");

        Grupo grupo = grupoMapper.requestAEntidad(request,curso,maestro,aula);

        grupoRepository.saveAndFlush(grupo);

        log.info("Nuevo grupo {} registrado ", grupo.getId());

        return grupoMapper.entidadAResponse(grupo);
    }

    @Override
    @Transactional
    public GrupoResponse actualizar(GrupoResquest request, Long id) {
        Grupo grupo = obtenerGrupo(id);

        Curso curso = obtenerCurso(request.idCurso());
        Maestro maestro = obtenerMaestro(request.idMaestro());
        Aula aula = obtenerAula(request.idAula());

        if (grupoRepository.existsByCursoIdAndMaestroIdAndAulaIdAndPeriodoAndIdNot(
                curso.getId(), maestro.getId(), aula.getId(), request.periodo().trim(), id))
            throw new ConflictException("Ya existe otro grupo con ese curso, maestro, aula y periodo");

        if(grupo.cambioEnDatos(curso, maestro, aula, request.periodo())) {
            log.info("Actualizando grupo con id: {}", id);

            grupo.actualizar(curso, maestro, aula, request.periodo());

            grupoRepository.saveAndFlush(grupo);

            log.info("Datos del grupo {} actualizados", id);
        }
        return grupoMapper.entidadAResponse(grupo);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Grupo grupo = obtenerGrupo(id);

        log.info("Eliminando grupo id: {}", id);

        if(inscripcionRepository.existsByGrupoId(id)){
            throw new EntidadRelacionadaException(
                    "No se puede eliminar el grupo ya que " +
                            "tiene alumnos inscritos"
            );
        }

        if(horarioRepository.existsByGrupoId(id)) {
            throw new EntidadRelacionadaException(
                    "No se puede eliminar el grupo ya que " +
                            "tiene horarios asignados"
            );
        }
            grupoRepository.delete(grupo);
            grupoRepository.flush();

            log.info("Grupo {} eliminado ", id);


    }

    private Grupo obtenerGrupo(Long id){
        return ServiceUtils.obtenerEntidadException(grupoRepository, id, Grupo.class);
    }

    private Curso obtenerCurso(Long id){
        return ServiceUtils.obtenerEntidadException(cursoRepository, id, Curso.class);
    }

    private Maestro obtenerMaestro(Long id){
        return ServiceUtils.obtenerEntidadException(maestroRepository, id, Maestro.class);
    }

    private Aula obtenerAula(Long id){
        return  ServiceUtils.obtenerEntidadException(aulaRepository, id, Aula.class);
    }
}
