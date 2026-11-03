package com.ramon.escuela.services.maestros;

import com.ramon.escuela.dto.maestros.MaestroRequest;
import com.ramon.escuela.dto.maestros.MaestroResponse;
import com.ramon.escuela.entities.Maestro;
import com.ramon.escuela.exceptions.ConflictException;
import com.ramon.escuela.exceptions.EntidadRelacionadaException;
import com.ramon.escuela.exceptions.RecursoNoEncontradoException;
import com.ramon.escuela.mappers.MaestroMapper;
import com.ramon.escuela.repositories.GrupoRepository;
import com.ramon.escuela.repositories.MaestroRepository;
import com.ramon.escuela.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MaestroServiceImpl implements MaestroService{

    private final MaestroRepository maestroRepository;
    private final GrupoRepository grupoRepository;
    private final MaestroMapper maestroMapper;


    @Override
    @Transactional(readOnly = true)
    public List<MaestroResponse> listar() {
        return maestroRepository.findAll().stream()
                .map(maestroMapper::entidadAResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public MaestroResponse obtenerPorId(Long id) {
        Maestro maestro = maestroRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Maestro no encontrado con ID: " + id));
        return maestroMapper.entidadAResponse(maestro);
    }

    @Override
    @Transactional
    public MaestroResponse registrar(MaestroRequest request) {

        log.info("Registrando nuevo maestro...");

        validarDatosUnicos(request);

        Maestro maestro = maestroMapper.requestAEntidad(request);

        maestroRepository.saveAndFlush(maestro);

        log.info("Nuevo maestro {} registrado", maestro.obtenerNombreCompleto());

        return maestroMapper.entidadAResponse(maestro);
    }

    @Override
    @Transactional
    public MaestroResponse actualizar(MaestroRequest request, Long id) {
        Maestro maestro = obtenerMaestro(id);

        log.info("Actualizando maestro con id: {}",id);

        validarCambiosUnicos(request,id);

        maestro.actualizar(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                request.email(),
                request.telefono()
        );

        maestroRepository.saveAndFlush(maestro);

        log.info("Maestro {} actualizado ", maestro.obtenerNombreCompleto());

        return maestroMapper.entidadAResponse(maestro);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Maestro maestro = obtenerMaestro(id);

        log.info("Eliminado maestro con id: {}",id);

        if(grupoRepository.existsByMaestroId(id)){
            throw new EntidadRelacionadaException(
                    "No se puede eliminar el maestro ya que tiene grupos asignados"
            );
        }

        maestroRepository.delete(maestro);
        maestroRepository.flush();

        log.info("Maestro {} eliminado", maestro.obtenerNombreCompleto());

    }

    private Maestro obtenerMaestro(Long id){
        return ServiceUtils.obtenerEntidadException(maestroRepository,id,Maestro.class);
    }

    public void validarDatosUnicos(MaestroRequest request){
        log.info("Validando email unico....");

        if(maestroRepository.existsByEmailIgnoreCase(request.email().trim()))
            throw new ConflictException("Ya existe un maestro registrado con el email: " + request.email());

        log.info("Validando cambio en telefono unico....");

        if(maestroRepository.existsByTelefono(request.telefono().trim()))
            throw new ConflictException("Ya existe un maestro registrado con el telefono: " + request.telefono());
    }

    public void validarCambiosUnicos(MaestroRequest request, Long id){
        log.info("Validando cambio de gmail unico....");

        if(maestroRepository.existsByEmailIgnoreCaseAndIdNot(request.email().trim(), id))
            throw new ConflictException("Ya existe un maestro registrado con el email: " + request.email());

        log.info("Validando cambio en telefono unico...");

        if(maestroRepository.existsByTelefonoAndIdNot(request.telefono().trim(), id))
            throw new ConflictException("Ya existe un maestro registrado con el telefono: " + request.telefono());
    }
}
