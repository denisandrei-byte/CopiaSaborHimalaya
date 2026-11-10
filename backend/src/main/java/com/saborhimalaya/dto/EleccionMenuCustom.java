package com.saborhimalaya.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saborhimalaya.entity.Categoria;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.Constraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
public class EleccionMenuCustom {

    @JsonIgnore
    private static final String[] singular = { "entrante", "principal", "postre", "bebida" };
    @JsonIgnore
    private static final String[] plural = { "entrantes", "principales", "postres", "bebidas" };

    @NotNull
    private Long idPlato;
    @NotBlank
    @Pattern(regexp = "^(Postre|Principal|Entrante|Bebida)$", message = "La eleccion de Menú no esninguna categoría definida")
    private String opcion;

    public EleccionMenuCustom(Long idPlato, String opcion) {
        this.idPlato = idPlato;
        this.opcion = opcion;
    }

    @JsonIgnore
    public boolean compararEntrePlatoYEleccion(Categoria categoria) {
        int ind = sacaIndexSingular();
        if (categoria == null || ind < 0)
            return false;
        boolean sonIguales = false;
        if (plural[ind].equals(categoria.getNombre().toLowerCase()))
            sonIguales = true;
        return sonIguales;
    }

    @JsonIgnore
    private int sacaIndexSingular() {
        int index = -1;
        for (int i = 0; i < singular.length; i++) {
            if (singular[i].equals(opcion.toLowerCase()))
                index = i;
        }
        return index;
    }
}
