package com.saborhimalaya.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Convierte las excepciones de la aplicación en respuestas HTTP
@RestControllerAdvice
public class GestorErrores {

    @ExceptionHandler(PlatoNoEncontradoException.class)
    public ProblemDetail platoNoEncontrado(PlatoNoEncontradoException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(DatosMenuNoValidosException.class)
    public ProblemDetail datosMenuNoValidos(DatosMenuNoValidosException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(PedidoNoEncontradoException.class)
    public ProblemDetail pedidoNoEncontrado(PedidoNoEncontradoException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(PedidosNoEncontradosException.class)
    public ProblemDetail pedidoNoEncontrado(PedidosNoEncontradosException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(UsuarioNoExisteException.class)
    public ProblemDetail pedidoNoEncontrado(UsuarioNoExisteException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(PedidoInvalidoException.class)
    public ProblemDetail pedidoInvalido(PedidoInvalidoException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
