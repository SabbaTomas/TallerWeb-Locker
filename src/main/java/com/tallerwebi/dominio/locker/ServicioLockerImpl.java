package com.tallerwebi.dominio.locker;

import com.tallerwebi.dominio.locker.Enum.TipoLocker;
import com.tallerwebi.util.Haversine;
import com.tallerwebi.dominio.locker.excepciones.LockerNoEncontrado;
import com.tallerwebi.dominio.locker.excepciones.ParametrosDelLockerInvalidos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServicioLockerImpl implements ServicioLocker {

    private final RepositorioDatosLocker lockerRepository;

    @Autowired
    public ServicioLockerImpl(RepositorioDatosLocker lockerRepository) {

        this.lockerRepository = lockerRepository;
    }

    @Override
    public void crearLocker(Locker locker) {
        if (locker == null || locker.getTipo() == null) {
            throw new ParametrosDelLockerInvalidos("Locker no puede tener parámetros nulos");
        }

        if (locker.getLatitud() == null || locker.getLongitud() == null || locker.getCodigo_postal() == null) {
            generarValoresAleatoriosParaLocker(locker);
        }

        if (locker.getDescripcion() == null || locker.getDescripcion().isEmpty()) {
            locker.setDescripcion("Descripción por defecto");
        }

        locker.setSeleccionado(true);

        lockerRepository.guardar(locker);
    }


    private void generarValoresAleatoriosParaLocker(Locker locker) {
        Random random = new Random();

        double centroLatitud = -34.6500;
        double centroLongitud = -58.5500;
        double rangoEnKilometros = 5;

        double latitud;
        double longitud;
        do {
            latitud = centroLatitud + (random.nextDouble() * 2 - 1) * (rangoEnKilometros / 110.574);
            longitud = centroLongitud + (random.nextDouble() * 2 - 1) * (rangoEnKilometros / (111.320 * Math.cos(Math.toRadians(centroLatitud))));
        } while (Haversine.distance(centroLatitud, centroLongitud, latitud, longitud) > rangoEnKilometros);

        List<String> codigosPostales = Arrays.asList("1704", "1754", "1706", "1702");
        String codigoPostal = codigosPostales.get(random.nextInt(codigosPostales.size()));

        locker.setLatitud(latitud);
        locker.setLongitud(longitud);
        locker.setCodigo_postal(codigoPostal);
    }


    @Override
    public void actualizarLocker(Long idLocker, TipoLocker tipoLocker) {
        if (idLocker == null || idLocker <= 0) {
            throw new LockerNoEncontrado("No se encontró ningún locker con el ID proporcionado: " + idLocker);
        }
        if (tipoLocker == null) {
            throw new ParametrosDelLockerInvalidos("Tipo de locker no puede ser nulo");
        }
        Locker locker = lockerRepository.obtenerLockerPorId(idLocker);
        if (locker != null) {
            locker.setTipo(tipoLocker);
            lockerRepository.actualizar(idLocker, tipoLocker);
        } else {
            throw new LockerNoEncontrado("No se encontró ningún locker con el ID proporcionado: " + idLocker);
        }
    }

    @Override
    public void eliminarLocker(Long idLocker) {
        if (idLocker == null || idLocker <= 0) {
            throw new ParametrosDelLockerInvalidos("ID de locker inválido");
        }
        lockerRepository.eliminar(idLocker);
    }

    @Override
    public List<Locker> obtenerLockersPorTipo(TipoLocker tipoLocker) {
        if (tipoLocker == null) {
            throw new ParametrosDelLockerInvalidos("Tipo de locker no puede ser nulo");
        }
        return lockerRepository.obtenerLockersPorTipo(tipoLocker);
    }

    @Override
    public Locker obtenerLockerPorId(Long idLocker) {
        Locker locker = lockerRepository.obtenerLockerPorId(idLocker);
        if (locker == null) {
            throw new LockerNoEncontrado("Locker no encontrado con ID: " + idLocker);
        }
        return locker;
    }

    @Override
   public List<Locker> obtenerLockersCercanos(double latitud, double longitud, double radio) {
        return lockerRepository.encontrarLockersPorCercania(latitud, longitud, radio);
    }

    @Override
    public List<Locker> obtenerLockersPorCodigoPostal(String codigoPostal) {

        if (codigoPostal == null || codigoPostal.isEmpty() ) {
            throw new ParametrosDelLockerInvalidos("Código postal no puede ser nulo o vacío");
        }
        return lockerRepository.obtenerLockersPorCodigoPostal(codigoPostal);
    }

    @Override
    public List<Locker> obtenerLockersSeleccionados() {
        return lockerRepository.obtenerSeleccionados();
    }


    @Override
    public List<Locker> buscarLockers(String codigoPostal, Double latitud, Double longitud, Double radio) {
        List<Locker> lockers;

        if (codigoPostal != null && !codigoPostal.isEmpty()) {
            lockers = obtenerLockersPorCodigoPostal(codigoPostal);
        } else if (latitud != null && longitud != null && radio != null) {
            lockers = obtenerLockersCercanos(latitud, longitud, radio);
        } else {
            lockers = obtenerLockersSeleccionados();
        }

        if (lockers == null || lockers.isEmpty()) {
            lockers = new ArrayList<>();
        } else {
            if (latitud != null && longitud != null) {
                for (Locker locker : lockers) {
                    double distancia = Haversine.distance(latitud, longitud, locker.getLatitud(), locker.getLongitud());
                    locker.setDistancia(distancia);
                }
            }
        }

        return lockers;
    }

    @Override
    public void eliminarTodos() {
        lockerRepository.eliminarTodos();
    }



}