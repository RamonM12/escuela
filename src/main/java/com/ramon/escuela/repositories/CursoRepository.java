package com.ramon.escuela.repositories;

import com.ramon.escuela.entities.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Curso, Long> {
    boolean existsByNombreCursos(String nombre);
    boolean existsByNombreCursosAndIdNot(String nombre, Long id);
}
