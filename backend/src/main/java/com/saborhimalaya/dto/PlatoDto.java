package com.saborhimalaya.dto;

import java.math.BigDecimal;

import com.saborhimalaya.entity.Categoria;
import com.saborhimalaya.entity.Plato;

// US13: plato mostrado en el listado de platos para el menú (P14)
public record PlatoDto(
        Long idPlato,
        String nombre,
        String descripcion,
        BigDecimal precio,
        Categoria categoria,
        boolean enCarta) {

    public static PlatoDto desde(Plato plato) {
        return new PlatoDto(plato.getIdPlato(), plato.getNombre(), plato.getDescripcion(), plato.getPrecio(),
                plato.getCategoria(), plato.isEnCarta());
    }
}
