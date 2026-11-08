package com.saborhimalaya.exception;

public class PedidoNoEncontradoException extends RuntimeException {
    public PedidoNoEncontradoException(Long idLong) {
        super("Pedido: " + idLong + " no existe");
    }
}
