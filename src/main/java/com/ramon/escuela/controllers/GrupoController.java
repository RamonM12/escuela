package com.ramon.escuela.controllers;

import com.ramon.escuela.dto.grupo.GrupoResponse;
import com.ramon.escuela.dto.grupo.GrupoResquest;
import com.ramon.escuela.services.grupo.GrupoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/grupos")
@Tag(name = "API de grupos", description = "Metodos para la gestion de los grupos")
public class GrupoController extends CrudController<GrupoResquest, GrupoResponse, GrupoService>{
    public GrupoController(GrupoService service) {
        super(service);
    }
}
