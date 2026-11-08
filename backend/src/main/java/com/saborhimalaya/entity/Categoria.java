package com.saborhimalaya.entity;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

// Categorías de la carta. En el JSON se usa el nombre ("Entrantes", "Postres"...).
// Para añadir una categoría nueva basta con añadirla aquí; si no es entrante, ensalada,
// postre ni bebida, se podrá usar como plato principal del menú diario (CA13.1).
public enum Categoria {

    ENTRANTES("Entrantes"),
    ENSALADAS("Ensaladas"),
    PRINCIPALES("Principales"),
    ESPECIALIDADES("Especialidades"),
    POSTRES("Postres"),
    BEBIDAS("Bebidas");

    private final String nombre;

    Categoria(String nombre) {
        this.nombre = nombre;
    }

    @JsonValue
    public String getNombre() {
        return nombre;
    }

    // Acepta el nombre ("Entrantes") o la constante ("ENTRANTES"), sin distinguir mayúsculas
    @JsonCreator
    public static Categoria desde(String valor) {
        if (valor != null) {
            String buscado = valor.trim().toLowerCase(Locale.ROOT);
            for (Categoria categoria : values()) {
                if (categoria.nombre.toLowerCase(Locale.ROOT).equals(buscado)
                        || categoria.name().toLowerCase(Locale.ROOT).equals(buscado)) {
                    return categoria;
                }
            }
        }
        throw new IllegalArgumentException("Categoría no válida: " + valor);
    }
}
