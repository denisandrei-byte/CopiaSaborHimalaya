package com.saborhimalaya.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class CategoriaTest {

    @Test
    void reconoceElNombreSinDistinguirMayusculas() {
        assertThat(Categoria.desde("Entrantes")).isEqualTo(Categoria.ENTRANTES);
        assertThat(Categoria.desde(" ensaladas ")).isEqualTo(Categoria.ENSALADAS);
        assertThat(Categoria.desde("POSTRES")).isEqualTo(Categoria.POSTRES);
    }

    @Test
    void usaElNombreParaMostrarse() {
        assertThat(Categoria.BEBIDAS.getNombre()).isEqualTo("Bebidas");
    }

    @Test
    void rechazaCategoriasQueNoExisten() {
        assertThatThrownBy(() -> Categoria.desde("Entrante")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Categoria.desde(null)).isInstanceOf(IllegalArgumentException.class);
    }
}
