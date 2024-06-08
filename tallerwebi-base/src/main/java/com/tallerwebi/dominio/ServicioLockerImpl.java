package com.tallerwebi.dominio;

import com.tallerwebi.dominio.excepcion.LockerNoEncontrado;
import com.tallerwebi.dominio.excepcion.ParametrosDelLockerInvalidos;
import com.tallerwebi.dominio.locker.Haversine;
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
            throw new ParametrosDelLockerInvalidos("Locker no puede tener parámetros nulos");
        }
        lockerRepository.guardar(locker);
    }

    @Override
    @Transactional
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
    @Transactional
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

    public Locker obtenerLockerPorId(Long idLocker) {
        if (idLocker == null || idLocker <= 0) {
            throw new ParametrosDelLockerInvalidos("ID de locker inválido");
        }
        return lockerRepository.obtenerLockerPorId(idLocker);
    }

    @Transactional
    public List<Locker> obtenerLockersCercanos(double latitud, double longitud, double radio) {
        return lockerRepository.encontrarLockersPorCercania(latitud, longitud, radio);
    }

    @Transactional
    public List<Locker> obtenerLockersPorCodigoPostal(String codigoPostal) {

        if (codigoPostal == null || codigoPostal.isEmpty() ) {
            throw new ParametrosDelLockerInvalidos("Código postal no puede ser nulo o vacío");
        }
        return lockerRepository.obtenerLockersPorCodigoPostal(codigoPostal);
    }

    @Transactional
    public List<Locker> obtenerLockersSeleccionados() {
        return lockerRepository.obtenerSeleccionados();
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
        } else {
            if (latitud != null && longitud != null) {
                for (Locker locker : lockers) {
                    double distancia = Haversine.distance(latitud, longitud, locker.getLatitud(), locker.getLongitud());
                    locker.setDistancia(distancia); // Asume que la clase Locker tiene un campo de distancia
                }
            }
        }

        return lockers;
    }



    @Transactional
    public void eliminarTodos() {
        lockerRepository.eliminarTodos();
    }



}