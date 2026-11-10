package com.saborhimalaya.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

/**
 * EleccionesMenu
 */
@Data
@AllArgsConstructor
public class EleccionesMenu {
    @NotNull
    @Valid
    private EleccionMenuCustom eleccionMenu1;
    @NotNull
    @Valid
    private EleccionMenuCustom eleccionMenu2;
    @NotNull
    @Valid
    private EleccionMenuCustom eleccionMenu3;
    @NotNull
    @Valid
    private EleccionMenuCustom eleccionMenu4;

    @JsonIgnore
    public boolean sonValidasLasElecciones() {
        boolean sonValidas = false;
        String nuevaCadena = eleccionMenu1.getOpcion() + " " + eleccionMenu2.getOpcion() + " "
                + eleccionMenu3.getOpcion() + " " + eleccionMenu4.getOpcion();
        if (nuevaCadena.contains("Principal")
                && nuevaCadena.contains("Postre") && nuevaCadena.contains("Bebida")
                && nuevaCadena.contains("Entrante"))
            sonValidas = true;
        return sonValidas;
    }
}
