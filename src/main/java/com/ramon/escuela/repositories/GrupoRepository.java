package com.ramon.escuela.repositories;

import com.ramon.escuela.entities.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    boolean existsByMaestroId(Long id);

    boolean existsByCursoId(Long id);

    boolean existsByAulaId(Long id);

    boolean existsByCursoIdAndMaestroIdAndAulaIdAndPeriodo(Long cursoId, Long maestroId, Long aulaId, String periodo);
    boolean existsByCursoIdAndMaestroIdAndAulaIdAndPeriodoAndIdNot(
            Long cursoId, Long maestroId, Long aulaId, String periodo, Long id);
}
