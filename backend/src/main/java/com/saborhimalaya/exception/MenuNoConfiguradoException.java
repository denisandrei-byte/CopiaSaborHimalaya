package com.saborhimalaya.exception;

/**
 * MenuNoConfiguradoException
 */
public class MenuNoConfiguradoException extends RuntimeException {
  public MenuNoConfiguradoException(){
    super("No hay menu configurado para hoy");
  }
}
