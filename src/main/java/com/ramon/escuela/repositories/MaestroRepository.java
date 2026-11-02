package com.ramon.escuela.repositories;

import com.ramon.escuela.entities.Maestro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaestroRepository extends JpaRepository<Maestro, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existByTelefono(String telefono);

    boolean existsByEmailIgnoreCaseAndIdNot(String emai, Long id);

    boolean existsByTelefonoAndIdNot(String telefono, Long id);

}
