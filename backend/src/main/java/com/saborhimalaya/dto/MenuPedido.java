package com.saborhimalaya.dto;

import java.time.LocalDateTime;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * MenuPedido
 */
@Data
@AllArgsConstructor
public class MenuPedido {
    @NotNull
    private LocalDateTime fechaMenu;
    @NotNull
    @Valid
    private EleccionesMenu elecciones;
    @NotNull
    @Positive(message = "Cantidad debe ser positiva")
    private Integer cantidad;
}
