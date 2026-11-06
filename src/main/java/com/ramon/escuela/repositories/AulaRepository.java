package com.ramon.escuela.repositories;

import com.ramon.escuela.entities.Aula;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AulaRepository extends JpaRepository<Aula, Long> {
    boolean existsByNombre (String nombre);
    boolean existsByNombreAndIdNot(String nombre, Long id);
}
