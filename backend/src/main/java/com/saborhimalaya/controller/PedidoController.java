package com.saborhimalaya.controller;

import java.net.URI;
import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.saborhimalaya.dto.PedidoRequest;
import com.saborhimalaya.entity.Pedido;
import com.saborhimalaya.service.PedidoService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/pedidos")
@AllArgsConstructor
public class PedidoController {

    private final PedidoService service;

    @PostMapping
    public ResponseEntity<Void> CrearPedido(@Valid @RequestBody PedidoRequest request, Principal principal) {
        Pedido pedido = service.AnadirPedido(request, principal.getName());
        URI location = MvcUriComponentsBuilder
                .fromController(PedidoController.class)
                .path("/{id}")
                .buildAndExpand(pedido.getIdPedido())
                .toUri();
        return ResponseEntity.created(location).build();
    }

}