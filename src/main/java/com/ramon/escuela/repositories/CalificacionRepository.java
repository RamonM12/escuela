package com.ramon.escuela.repositories;

import com.ramon.escuela.entities.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {
    boolean existsByInscripcionId(Long inscripcionId);

    boolean existsByInscripcionIdAndIdNot(Long inscripcionId, Long id);
}