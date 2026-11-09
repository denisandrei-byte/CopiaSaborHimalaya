package com.saborhimalaya.dto;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * PlatoPedido
 */
@Data
@AllArgsConstructor
public class PlatoPedido {

    @NotNull
    private Long idPlato;
    @NotNull
    @Positive(message = "Cantidad debe ser positiva")
    private Integer cantidad;
}
