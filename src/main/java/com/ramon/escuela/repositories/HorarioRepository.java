package com.ramon.escuela.repositories;

import com.ramon.escuela.entities.Horario;
import com.ramon.escuela.enums.DiaSemana;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HorarioRepository extends JpaRepository<Horario, Long> {
    boolean existsByGrupoId(Long id);
    List<Horario> findByGrupoIdAndDiaSemana(Long grupoId, DiaSemana diaSemana);

    List<Horario> findByGrupoAulaIdAndGrupoPeriodoAndDiaSemana(Long aulaId, String periodo, DiaSemana diaSemana);
}
