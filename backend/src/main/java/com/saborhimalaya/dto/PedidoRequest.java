package com.saborhimalaya.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * PedidoRequest
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoRequest {

    private List<MenuPedido> menus;
    private List<PlatoPedido> platos;

}
