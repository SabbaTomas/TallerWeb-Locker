package com.tallerwebi.dominio.reserva.excepciones;

public class ReservaNoEncontradaException extends RuntimeException {
    public ReservaNoEncontradaException(String message) {
        super(message);
    }
}
