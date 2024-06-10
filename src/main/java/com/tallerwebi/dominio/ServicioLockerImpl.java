package com.tallerwebi.dominio;

import com.tallerwebi.dominio.locker.ServicioLocker;
import com.tallerwebi.dominio.locker.TipoLocker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServicioLockerImpl implements ServicioLocker {
    private final RepositorioDatosLockerImpl lockerRepository;

    @Autowired
    public ServicioLockerImpl(RepositorioDatosLockerImpl lockerRepository) {
        this.lockerRepository = lockerRepository;
    }

    @Transactional
    public void crearLocker(Locker locker) {
        if (locker == null || locker.getTipo() == null || locker.getLatitud() == null || locker.getLongitud() == null || locker.getCodigo_postal() == null) {
            throw new IllegalArgumentException("Locker no puede tener parámetros nulos");
        }
        lockerRepository.guardar(locker);
    }

    @Override
    public void actualizarLocker(Long idLocker, TipoLocker tipoLocker) {
        if (idLocker == null || idLocker <= 0) {
            throw new IllegalArgumentException("No se encontró ningún locker con el ID proporcionado: " + idLocker);
        }
        if (tipoLocker == null) {
            throw new IllegalArgumentException("Tipo de locker no puede ser nulo");
        }
        Locker locker = lockerRepository.obtenerPorId(idLocker);
        if (locker != null) {
            locker.setTipo(tipoLocker);
            lockerRepository.actualizar(locker);
        } else {
            throw new IllegalArgumentException("No se encontró ningún locker con el ID proporcionado: " + idLocker);
        }
    }

    @Override
    public void eliminarLocker(Long idLocker) {
        if (idLocker == null || idLocker <= 0) {
            throw new IllegalArgumentException("ID de locker inválido");
        }
        lockerRepository.eliminar(idLocker);
    }

    @Override
    public List<Locker> obtenerLockersPorTipo(TipoLocker tipoLocker) {
        if (tipoLocker == null) {
            throw new IllegalArgumentException("Tipo de locker no puede ser nulo");
        }
        return lockerRepository.obtenerLockersPorTipo(tipoLocker);
    }

    public Locker obtenerLockerPorId(Long idLocker) {
        if (idLocker == null || idLocker <= 0) {
            throw new IllegalArgumentException("ID de locker inválido");
        }
        return lockerRepository.obtenerPorId(idLocker);
    }

    @Transactional
    public List<Locker> obtenerLockersPorCodigoPostal(String codigoPostal) {
        if (codigoPostal == null || codigoPostal.isEmpty()) {
            throw new IllegalArgumentException("Código postal no puede ser nulo o vacío");
        }
        return lockerRepository.obtenerLockersPorCodigoPostal(codigoPostal);
    }

    @Transactional(readOnly = true)
    public List<Locker> obtenerLockersSeleccionados() {
        return lockerRepository.obtenerSeleccionados();
    }

    @Transactional
    public void eliminarTodos() {
        lockerRepository.eliminarTodos();
    }

    public List<Locker> obtenerLockersCercanos(double latitud, double longitud, double radio) {
        double rangoLat = radio / 111.0;
        double rangoLon = radio / (111.0 * Math.cos(Math.toRadians(latitud)));
        return lockerRepository.obtenerLockersPorRangoDeCoordenadas(latitud - rangoLat, latitud + rangoLat, longitud - rangoLon, longitud + rangoLon);
    }

    @Transactional
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
        }

        return lockers;
    }


}