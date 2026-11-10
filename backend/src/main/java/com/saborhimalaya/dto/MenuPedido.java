package com.saborhimalaya.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * MenuPedido
 */
@Data
@AllArgsConstructor
public class MenuPedido {
    @NotNull
    private LocalDate fechaMenu;
    @NotNull
    @Valid
    private EleccionesMenu elecciones;
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
