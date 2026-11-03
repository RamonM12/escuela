package com.ramon.escuela.utils;

import com.ramon.escuela.exceptions.RecursoNoEncontradoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;

@Slf4j
public class ServiceUtils {
    public static<E,ID> E obtenerEntidadException(
            JpaRepository<E,ID> repository,
            ID id,
            Class<E> clase
    ){
        log.info("Buscando {} con id: {}", clase.getSimpleName(),id);

        return repository.findById(id).orElseThrow(() ->
                new RecursoNoEncontradoException(
                        clase.getSimpleName() +
                                "no encontrado con id: " + id));

    }
}
