package com.saborhimalaya.dto;

import java.util.List;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PedidoRequest
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoRequest {
    @Valid
    private List<@Valid MenuPedido> menus;
    @Valid
    private List<@Valid PlatoPedido> platos;

}
