package com.ramon.escuela.repositories;

import com.ramon.escuela.entities.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {
    boolean existsByAlumnoId(Long id);
    boolean existsByGrupoId(Long id);
    boolean existsByAlumnoIdAndGrupoId(Long alumnoId, Long grupoId);
    boolean existsByAlumnoIdAndGrupoIdAndIdNot(Long alumnoId, Long grupoId, Long id);
}
