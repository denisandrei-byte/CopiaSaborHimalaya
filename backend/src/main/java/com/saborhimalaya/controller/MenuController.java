package com.saborhimalaya.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.saborhimalaya.dto.ConfigurarMenuRequest;
import com.saborhimalaya.dto.PlatoDto;
import com.saborhimalaya.entity.Menu;
import com.saborhimalaya.service.MenuService;

import jakarta.validation.Valid;

// US12, US13
@RestController
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    // US12: configura (o sobrescribe) el menú del día indicado
    @PostMapping
    public ResponseEntity<Void> configurarMenu(@Valid @RequestBody ConfigurarMenuRequest request) {
        Menu menu = menuService.configurarMenu(request);
        return ResponseEntity.created(URI.create("/api/menus/" + menu.getIdMenu())).build();
    }

    // US13 (P14): platos de la carta y del restaurante que se pueden elegir para el menú.
    // tipo: Entrante | Principal | Postre; origen: carta | fuera. Ambos opcionales.
    @GetMapping("/platos")
    public List<PlatoDto> listarPlatosParaMenu(@RequestParam(required = false) String tipo,
            @RequestParam(required = false) String origen) {
        return menuService.listarPlatosParaMenu(tipo, origen);
    }
}
