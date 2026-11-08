package com.saborhimalaya.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

/**
 * PlatoPedido
 */
@Data
@AllArgsConstructor
public class PlatoPedido {

    @NonNull
    private Long idPlato;
    @NonNull
    private Integer cantidad;
}
