package com.tallerwebi.dominio.reserva.excepciones;

public class ReservaActivaExistenteException extends Exception {
    public ReservaActivaExistenteException(String mensaje) {
        super(mensaje);
    }
}