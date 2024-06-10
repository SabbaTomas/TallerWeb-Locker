package com.tallerwebi.dominio.locker;

import com.tallerwebi.dominio.Locker;

import java.util.List;

public interface ServicioLocker {



    public void crearLocker(Locker locker);
    void actualizarLocker(Long idLocker, TipoLocker tipoLocker);
    void eliminarLocker(Long idLocker);
    public void eliminarTodos();

    List<Locker> obtenerLockersPorTipo(TipoLocker tipoLocker);
    Locker obtenerLockerPorId(Long lockerId);

    List<Locker> obtenerLockersSeleccionados();

    List<Locker> obtenerLockersPorCodigoPostal(String codigoPostal);

    List<Locker> obtenerLockersCercanos(double latitud, double longitud, double radio);
    public List<Locker> buscarLockers(String codigoPostal, Double latitud, Double longitud, Double radio);

}
