package com.tallerwebi.dominio.locker.excepciones;

public class LockerNoEncontrado extends RuntimeException{

    public LockerNoEncontrado(String message){
        super(message);
    }

}
