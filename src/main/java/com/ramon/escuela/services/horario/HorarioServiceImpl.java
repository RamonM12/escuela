package com.ramon.escuela.services.horario;

import com.ramon.escuela.dto.horario.HorarioResponse;
import com.ramon.escuela.dto.horario.HorarioRequest;
import com.ramon.escuela.entities.Grupo;
import com.ramon.escuela.entities.Horario;
import com.ramon.escuela.enums.DiaSemana;
import com.ramon.escuela.exceptions.ConflictException;
import com.ramon.escuela.mappers.HorarioMapper;
import com.ramon.escuela.repositories.GrupoRepository;
import com.ramon.escuela.repositories.HorarioRepository;
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
public class HorarioServiceImpl implements HorarioService {

    private final HorarioRepository horarioRepository;
    private final HorarioMapper horarioMapper;
    private final GrupoRepository grupoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<HorarioResponse> listar() {
        log.info("Listando todos los horarios...");

        return horarioRepository.findAll().stream()
                .map(horarioMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HorarioResponse obtenerPorId(Long id) {
        return horarioMapper.entidadAResponse(obtenerHorario(id));
    }

    @Override
    @Transactional
    public HorarioResponse registrar(HorarioRequest request) {
        log.info("Registrando nuevo horario....");

        Grupo grupo = obtenerGrupo(request.idGrupo());

        Horario horario = horarioMapper.requestAEntidad(request, grupo);

        validarTraslapes(grupo, horario.getDiaSemana(),
                horario.getHoraInicio(), horario.getHoraFin(), null);

        horarioRepository.saveAndFlush(horario);

        log.info("Nuevo horario {} registrado", horario.getId());

        return horarioMapper.entidadAResponse(horario);
    }

    @Override
    @Transactional
    public HorarioResponse actualizar(HorarioRequest request, Long id) {
        Horario horario = obtenerHorario(id);
        Grupo grupo = obtenerGrupo(request.idGrupo());
        DiaSemana dia = DiaSemana.obtenerDiaSemanaPorDescripcion(request.dia());
        String horaInicio = request.horaInicio().trim();
        String horaFin = request.horaFin().trim();

        Horario.validarDatos(grupo, dia, horaInicio, horaFin);

        validarTraslapes(grupo, dia, horaInicio, horaFin, id);

        if (horario.cambioEnDatos(grupo, dia, horaInicio, horaFin)) {
            log.info("Actualizando horario con id: {}", id);

            horario.actualizar(grupo, dia, horaInicio, horaFin);

            horarioRepository.saveAndFlush(horario);

            log.info("Datos del horario {} actualizados", id);
        }

        return horarioMapper.entidadAResponse(horario);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Horario horario = obtenerHorario(id);

        log.info("Eliminando horario id: {}", id);

        horarioRepository.delete(horario);
        horarioRepository.flush();

        log.info("Horario {} eliminado", id);
    }

    private void validarTraslapes(Grupo grupo, DiaSemana dia, String horaInicio, String horaFin, Long idExcluir) {
        List<Horario> horariosDelGrupo =
                horarioRepository.findByGrupoIdAndDiaSemana(grupo.getId(), dia);

        if (hayTraslape(horariosDelGrupo, horaInicio, horaFin, idExcluir))
            throw new ConflictException("El horario se traslapa con otro horario del mismo grupo");

        List<Horario> horariosDelAula =
                horarioRepository.findByGrupoAulaIdAndGrupoPeriodoAndDiaSemana(
                        grupo.getAula().getId(), grupo.getPeriodo(), dia);

        if (hayTraslape(horariosDelAula, horaInicio, horaFin, idExcluir))
            throw new ConflictException("El horario se traslapa con otro horario de la misma aula en el mismo periodo");
    }

    private boolean hayTraslape(List<Horario> horarios, String horaInicio, String horaFin, Long idExcluir) {
        return horarios.stream()
                .filter(otro -> !otro.getId().equals(idExcluir))
                .anyMatch(otro -> otro.seTraslapaCon(horaInicio, horaFin));
    }

    private Horario obtenerHorario(Long id) {
        return ServiceUtils.obtenerEntidadException(horarioRepository, id, Horario.class);
    }

    private Grupo obtenerGrupo(Long id) {
        return ServiceUtils.obtenerEntidadException(grupoRepository, id, Grupo.class);
    }
}