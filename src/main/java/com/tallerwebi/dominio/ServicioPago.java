package com.tallerwebi.dominio;


import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;

public interface ServicioPago {

    String createPreference(String title, int quantity, double costoTotal, double penalizacion, int id) throws MPException, MPApiException;

    boolean verificarPagoConServidor(String collectionStatus);

    void registrarLaReservaComoAprobada(Long idReserva,String estado);

}

