package com.saborhimalaya.exception;

// US12, US13: datos del menú que no cumplen las reglas (cantidades, platos repetidos, tipo de opción, origen o categoría)
public class DatosMenuNoValidosException extends RuntimeException {

    public DatosMenuNoValidosException(String mensaje) {
        super(mensaje);
    }
}
