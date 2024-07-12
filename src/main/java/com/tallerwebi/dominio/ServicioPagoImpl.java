package com.tallerwebi.dominio;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import com.tallerwebi.dominio.reserva.RepositorioReserva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServicioPagoImpl implements ServicioPago {

    @Autowired
    private RepositorioReserva repositorioReserva;

    public ServicioPagoImpl(RepositorioReserva repositorioReserva) {
        this.repositorioReserva = repositorioReserva;
    }

    @Override
    public String createPreference(String title, int quantity, double costoTotal, double penalizacion, int id) throws MPException, MPApiException {
        MercadoPagoConfig.setAccessToken("TEST-4056254779598488-060816-3bb1669952911abfb0d38cfa865d2291-600975335");

        BigDecimal totalAmount = BigDecimal.valueOf(costoTotal).add(BigDecimal.valueOf(penalizacion));

        PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                .id(String.valueOf(id))
                .title(title)
                .quantity(quantity)
                .unitPrice(totalAmount)
                .currencyId("ARS")
                .build();

        List<PreferenceItemRequest> items = new ArrayList<>();
        items.add(itemRequest);

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                .success("http://localhost:8080/lockers/resultado")
                .pending("http://localhost:8080/lockers/resultado")
                .failure("http://localhost:8080/lockers/resultado")
                .build();

        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                .items(items)
                .backUrls(backUrls)
                .externalReference(String.valueOf(id))
                .build();

        PreferenceClient client = new PreferenceClient();
        try {
            Preference preference = client.create(preferenceRequest);
            return preference.getId();
        } catch (MPException | MPApiException e) {
            throw new RuntimeException("Error al crear la preferencia de Mercado Pago", e);
        }
    }

    @Override
    public boolean verificarPagoConServidor(String collectionStatus) {
        return "approved".equalsIgnoreCase(collectionStatus);
    }

    @Override
    public void registrarLaReservaComoAprobada(Long idReserva, String estado) {
        repositorioReserva.actualizarEstadoReserva(idReserva, estado);
    }
}
