package com.ramon.escuela.controllers;

import com.ramon.escuela.dto.horario.HorarioResponse;
import com.ramon.escuela.dto.horario.HorarioRequest;
import com.ramon.escuela.services.horario.HorarioService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/horarios")
@Tag(name = "API de horarios", description = "Metodos para la gestion de los horarios")
public class HorarioController extends CrudController<HorarioRequest, HorarioResponse, HorarioService>{
    public HorarioController(HorarioService service) {
        super(service);
    }
}
