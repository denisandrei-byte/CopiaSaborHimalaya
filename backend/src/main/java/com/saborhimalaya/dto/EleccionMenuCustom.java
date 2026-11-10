package com.saborhimalaya.dto;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;
import jakarta.validation.Constraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
@AllArgsConstructor
public class EleccionMenuCustom {

    @NotNull
    private Long idPlato;
    @NotBlank
    @Pattern(regexp = "^(Postres|Principales|Entrantes|Bebidas)$", message = "La eleccion de Menú no esninguna categoría definida")
    private String opcion;
}
