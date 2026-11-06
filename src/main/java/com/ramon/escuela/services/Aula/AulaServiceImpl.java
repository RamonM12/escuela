package com.ramon.escuela.services.Aula;

import com.ramon.escuela.dto.aula.AulaResponse;
import com.ramon.escuela.dto.aula.AulaRequest;
import com.ramon.escuela.entities.Aula;
import com.ramon.escuela.exceptions.ConflictException;
import com.ramon.escuela.exceptions.EntidadRelacionadaException;
import com.ramon.escuela.mappers.AulaMapper;
import com.ramon.escuela.repositories.AulaRepository;
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
public class AulaServiceImpl implements AulaService{

    private final AulaRepository aulaRepository;
    private final AulaMapper aulaMapper;
    private final GrupoRepository grupoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AulaResponse> listar() {
        log.info("Listando todas las aulas");

        return aulaRepository.findAll().stream()
                .map(aulaMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AulaResponse obtenerPorId(Long id) {
        return aulaMapper.entidadAResponse(obtenerAula(id));
    }

    @Override
    @Transactional
    public AulaResponse registrar(AulaRequest request) {
        log.info("Registrando nueva aula....");

        if (aulaRepository.existsByNombre(request.nombre().trim()))
            throw new ConflictException("Ya existe un aula con el nombre " + request.nombre());

        Aula aula = aulaMapper.requestAEntidad(request);

        aulaRepository.saveAndFlush(aula);

        log.info("Nueva aula {} registrada", aula.getNombre());

        return  aulaMapper.entidadAResponse(aula);
    }

    @Override
    @Transactional
    public AulaResponse actualizar(AulaRequest request, Long id) {
        Aula aula = obtenerAula(id);

        if (aulaRepository.existsByNombreAndIdNot(request.nombre().trim(), id))
            throw new ConflictException("Ya existe otra aula con el nombre " + request.nombre());

        if(aula.cambioEnDatos(
                request.nombre(),
                request.capacidad()
        )){
            log.info("Actualizando aula con id: {} ",id);

            aula.actualizar(
                    request.nombre(),
                    request.capacidad());

            aulaRepository.saveAndFlush(aula);

            log.info("Datos del aula {} actualizados ", aula.getNombre());
        }

        return aulaMapper.entidadAResponse(aula);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Aula aula = obtenerAula(id);

        log.info("Eliminando aula id: {}" , id);

        if(grupoRepository.existsByAulaId(id))
            throw new EntidadRelacionadaException(
                    "No se puede eliminar el aula ya que " +
                            "tiene grupos asignados."
            );

        aulaRepository.delete(aula);
        aulaRepository.flush();

        log.info("Aula {} eliminada", aula.getNombre());
    }

    private Aula obtenerAula(Long id){
        return ServiceUtils.obtenerEntidadException(aulaRepository, id, Aula.class);
    }
}
