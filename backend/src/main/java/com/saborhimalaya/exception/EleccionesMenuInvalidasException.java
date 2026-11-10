package com.saborhimalaya.exception;

public class EleccionesMenuInvalidasException extends RuntimeException {
    public EleccionesMenuInvalidasException() {
        super("Las elecciones del menú son invalidas");
    }

}
