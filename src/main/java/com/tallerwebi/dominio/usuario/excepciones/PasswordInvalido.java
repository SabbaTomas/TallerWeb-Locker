package com.tallerwebi.dominio.usuario.excepciones;

public class PasswordInvalido extends RuntimeException {

    public PasswordInvalido(String message) {
        super(message);
    }

    public PasswordInvalido(String message, Throwable cause) {
        super(message, cause);
    }
}
