package com.tallerwebi.dominio.locker;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class ServicioGeocodificacion {

    private GeoApiContext context;

    public ServicioGeocodificacion(@Value("${google.maps.api.key}") String apiKey) {
        this.context = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
    }

    public Coordenadas obtenerCoordenadasPorCodigoPostal(String codigoPostal) throws Exception {
        GeocodingResult[] results = GeocodingApi.geocode(context, codigoPostal).await();
        if (results.length > 0) {
            double latitud = results[0].geometry.location.lat;
            double longitud = results[0].geometry.location.lng;
            return new Coordenadas(latitud, longitud);
        } else {
            throw new Exception("No se encontraron coordenadas para el código postal proporcionado.");
        }
    }

    public static class Coordenadas {
        public double latitud;
        public double longitud;

        public Coordenadas(double latitud, double longitud) {
            this.latitud = latitud;
            this.longitud = longitud;
        }
    }
}

