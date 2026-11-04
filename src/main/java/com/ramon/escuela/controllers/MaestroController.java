package com.ramon.escuela.controllers;

import com.ramon.escuela.dto.maestros.MaestroRequest;
import com.ramon.escuela.dto.maestros.MaestroResponse;
import com.ramon.escuela.services.maestros.MaestroService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/maestros")
@Tag(name = "API MAestros", description = "Metodos para gestion de maestros")
public class MaestroController extends CrudController<MaestroRequest, MaestroResponse, MaestroService>{

    public MaestroController(MaestroService service) {
        super(service);
    }
}