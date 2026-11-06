package com.ramon.escuela.controllers;

import com.ramon.escuela.dto.calificacion.CalificacionRequest;
import com.ramon.escuela.dto.calificacion.CalificacionResponse;
import com.ramon.escuela.services.Calificacion.CalificacionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/calificaciones")
@Tag(name = "API de calificaciones", description = "Metodos para la gestion de las calificaciones")
public class CalificacionController extends CrudController<CalificacionRequest, CalificacionResponse, CalificacionService> {
    public CalificacionController(CalificacionService service) {
        super(service);
    }
}