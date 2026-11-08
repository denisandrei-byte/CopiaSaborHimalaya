package com.saborhimalaya.dto;

import com.saborhimalaya.entity.TipoOpcion;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EleccionMenuCustom {

    private Long idPlato;
    private String opcion;
}
