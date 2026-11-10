package com.saborhimalaya.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * MenuPedido
 */
@Data
@NoArgsConstructor
public class MenuPedido {
    @NotNull
    @JsonIgnore
    private LocalDate fechaMenu;
    @NotNull
    @Valid
    private EleccionesMenu elecciones;
    @NotNull
    @Positive(message = "La cantidad de menus debe ser positiva")
    @Max(value = 10000)
    private Integer cantidad;

    public MenuPedido(EleccionesMenu eleccionesMenu, Integer cantidad) {
        LocalDate localDate = LocalDate.now();
        fechaMenu = LocalDate.of(localDate.getYear(), localDate.getMonth(), localDate.getDayOfMonth());
        this.elecciones = eleccionesMenu;
        this.cantidad = cantidad;
    }
}
