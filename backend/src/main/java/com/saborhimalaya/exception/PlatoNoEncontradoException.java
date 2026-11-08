package com.saborhimalaya.exception;

public class PlatoNoEncontradoException extends RuntimeException {

    public PlatoNoEncontradoException(Long idPlato) {
        super("No existe el plato con id " + idPlato);
    }
}
