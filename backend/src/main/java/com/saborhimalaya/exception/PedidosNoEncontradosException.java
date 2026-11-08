package com.saborhimalaya.exception;

/**
 * PedidosNoEncontradosException
 */

public class PedidosNoEncontradosException extends RuntimeException {
    public PedidosNoEncontradosException(String loginUsuario) {
        super("El usuario: " + loginUsuario + " no tiene pedidos");
    }
}
