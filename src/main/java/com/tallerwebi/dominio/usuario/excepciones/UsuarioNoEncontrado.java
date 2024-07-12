package com.tallerwebi.dominio.usuario.excepciones;

public class UsuarioNoEncontrado extends RuntimeException {

    public UsuarioNoEncontrado(String message) {
        super(message);
    }
}
