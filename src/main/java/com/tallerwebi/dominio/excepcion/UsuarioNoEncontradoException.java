package com.tallerwebi.dominio.excepcion;

public class UsuarioNoEncontradoException extends RuntimeException {
    public UsuarioNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}