package com.tallerwebi.dominio.excepcion;

public class ReservaActivaExistenteException extends Exception {
    public ReservaActivaExistenteException(String mensaje) {
        super(mensaje);
    }
}