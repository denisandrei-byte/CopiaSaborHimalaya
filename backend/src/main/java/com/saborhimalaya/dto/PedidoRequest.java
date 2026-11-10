package com.saborhimalaya.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
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
    @Size(max = 200)
    private List<@Valid MenuPedido> menus;
    @Valid
    @Size(max = 200)
    private List<@Valid PlatoPedido> platos;

}
