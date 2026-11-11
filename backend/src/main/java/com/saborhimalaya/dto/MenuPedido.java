package com.saborhimalaya.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
public class MenuPedido {
    @NotNull
    @JsonIgnore
    private final LocalDate fechaMenu = LocalDate.now(ZoneId.of("Europe/Madrid"));
    @NotNull
    @Valid
    private EleccionesMenu elecciones;
    @NotNull
    @Positive(message = "La cantidad de menus debe ser positiva")
    @Max(value = 10000)
    private Integer cantidad;

    public MenuPedido(EleccionesMenu elecciones, Integer cantidad) {
        this.elecciones = elecciones;
        this.cantidad = cantidad;
    }
}
