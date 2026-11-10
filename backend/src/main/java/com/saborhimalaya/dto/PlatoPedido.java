package com.saborhimalaya.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.constraints.AssertTrue;
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
    private Integer cantidad;

    @JsonIgnore
    @AssertTrue(message = "La cantidad del plato debe ser mayor que cero")
    public boolean cantidadMayorQueCero() {
        if (cantidad != null && cantidad.intValue() <= 0)
            return false;
        else
            return true;
    }
}
