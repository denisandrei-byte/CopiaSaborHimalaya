package com.saborhimalaya.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class TipoOpcionTest {

    @Test
    void reconoceElNombreSinDistinguirMayusculas() {
        assertThat(TipoOpcion.desde("Entrante")).isEqualTo(TipoOpcion.ENTRANTE);
        assertThat(TipoOpcion.desde(" principal ")).isEqualTo(TipoOpcion.PRINCIPAL);
        assertThat(TipoOpcion.desde("POSTRE")).isEqualTo(TipoOpcion.POSTRE);
    }

    // En la BD se guarda la constante, que es lo que admite el CHECK de opcion_menu
    @Test
    void seGuardaConLaConstanteYSeMuestraConElNombre() {
        assertThat(TipoOpcion.ENTRANTE.name()).isEqualTo("ENTRANTE");
        assertThat(TipoOpcion.ENTRANTE.getNombre()).isEqualTo("Entrante");
    }

    @Test
    void rechazaTiposQueNoExisten() {
        assertThatThrownBy(() -> TipoOpcion.desde("Bebida")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> TipoOpcion.desde(null)).isInstanceOf(IllegalArgumentException.class);
    }
}
