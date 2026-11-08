package com.saborhimalaya.exception;

/**
 * UsuarioNoExisteException
 */
public class UsuarioNoExisteException extends RuntimeException {

    public UsuarioNoExisteException(String login) {
        super("El usuario: " + login + " no existe");
    }

}
