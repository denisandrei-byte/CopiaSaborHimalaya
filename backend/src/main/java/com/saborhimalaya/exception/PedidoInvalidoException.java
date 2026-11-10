package com.saborhimalaya.exception;

public class PedidoInvalidoException extends RuntimeException {

    public PedidoInvalidoException() {
        super("La peticion de creación de pedido es invalida");
    }
}
