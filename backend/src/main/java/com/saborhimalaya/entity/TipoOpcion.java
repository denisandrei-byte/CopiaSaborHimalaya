package com.saborhimalaya.entity;

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

// Tipos de opción del menú diario (US12, US13). En la BD se guarda la constante (ENTRANTE...),
// como exige el CHECK de opcion_menu; en la API se usa el nombre ("Entrante"...).
public enum TipoOpcion {

    ENTRANTE("Entrante"),
    PRINCIPAL("Principal"),
    POSTRE("Postre");

    private final String nombre;

    TipoOpcion(String nombre) {
        this.nombre = nombre;
    }

    @JsonValue
    public String getNombre() {
        return nombre;
    }

    // Acepta el nombre ("Entrante") o la constante ("ENTRANTE"), sin distinguir mayúsculas
    @JsonCreator
    public static TipoOpcion desde(String valor) {
        if (valor != null) {
            String buscado = valor.trim().toLowerCase(Locale.ROOT);
            for (TipoOpcion tipo : values()) {
                if (tipo.nombre.toLowerCase(Locale.ROOT).equals(buscado)
                        || tipo.name().toLowerCase(Locale.ROOT).equals(buscado)) {
                    return tipo;
                }
            }
        }
        throw new IllegalArgumentException("Tipo de opción no válido: " + valor);
    }
}
