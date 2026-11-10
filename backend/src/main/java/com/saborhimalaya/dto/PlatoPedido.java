package com.saborhimalaya.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
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
    @Positive(message = "La cantidad de platos debe ser positiva")
    @Max(value = 10000)
    private Integer cantidad;

}
