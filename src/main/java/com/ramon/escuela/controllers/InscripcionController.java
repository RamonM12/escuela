package com.ramon.escuela.controllers;

import com.ramon.escuela.dto.inscripcion.InscripcionRequest;
import com.ramon.escuela.dto.inscripcion.InscripcionResponse;
import com.ramon.escuela.services.Inscripcion.InscripcionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inscripciones")
@Tag(name = "API de Inscripcion", description = "Metodos para la gestion de inscripcion")
public class InscripcionController extends CrudController<InscripcionRequest, InscripcionResponse, InscripcionService>{
    public InscripcionController(InscripcionService service) {
        super(service);
    }
}
