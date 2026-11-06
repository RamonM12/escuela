package com.ramon.escuela.controllers;

import com.ramon.escuela.dto.cursos.CursoRequest;
import com.ramon.escuela.dto.cursos.CursoResponse;
import com.ramon.escuela.services.curso.CursoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cursos")
@Tag(name = "API de cursos", description = "Metodos para la gestion de los cursos")
public class CursoController extends CrudController<CursoRequest, CursoResponse, CursoService>{
    public CursoController(CursoService service) {
        super(service);
    }
}
