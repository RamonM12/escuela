package com.ramon.escuela.controllers;

import com.ramon.escuela.dto.aula.AulaResponse;
import com.ramon.escuela.dto.aula.AulaRequest;
import com.ramon.escuela.services.Aula.AulaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/aulas")
@Tag(name = "API de aulas", description = "Metodos para la gestion de las aulas")
public class AulaController extends CrudController<AulaRequest, AulaResponse, AulaService>{
    public AulaController(AulaService service) {
        super(service);
    }
}
