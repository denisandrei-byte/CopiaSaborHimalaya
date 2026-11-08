package com.saborhimalaya.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

/**
 * MenuPedido
 */
@Data
@AllArgsConstructor
public class MenuPedido {
    @NonNull
    private Long idMenu;
    @NonNull
    private EleccionesMenu elecciones;
    @NonNull
    private Integer cantidad;
}
