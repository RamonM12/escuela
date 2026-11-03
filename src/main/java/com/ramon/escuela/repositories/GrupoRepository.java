package com.ramon.escuela.repositories;

import com.ramon.escuela.entities.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    boolean existsByMaestroId(Long id);
}
