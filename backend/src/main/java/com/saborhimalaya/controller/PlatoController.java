package com.saborhimalaya.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.saborhimalaya.dto.CrearPlatoRequest;
import com.saborhimalaya.dto.PlatoDto;
import com.saborhimalaya.service.PlatoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/platos")
public class PlatoController {

    private final PlatoService platoService;

    public PlatoController(PlatoService platoService) {
        this.platoService = platoService;
    }

    @GetMapping("/carta")
    public List<PlatoDto> obtenerPlatosDeLaCarta() {
        return platoService.obtenerPlatosDeLaCarta();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlatoDto anadirPlato(@Valid @RequestBody CrearPlatoRequest request) {
        return platoService.anadirPlatoALaCarta(request);
    }
}
