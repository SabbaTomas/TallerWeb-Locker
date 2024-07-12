package com.tallerwebi.dominio.locker;


import com.tallerwebi.dominio.locker.Enum.TipoLocker;

import java.util.List;

public interface RepositorioDatosLocker  {

    void guardar(Locker locker);

    void  actualizar(Long idLocker, TipoLocker tipoLocker);

    void eliminar(Long idLocker);

    Locker obtenerLockerPorId(Long idLocker);

    List<Locker> obtenerLockersPorTipo(TipoLocker tipoLocker);

    List<Locker> obtenerSeleccionados();

    void eliminarTodos();

    List<Locker> obtenerLockersPorCodigoPostal(String codigoPostal);

    List<Locker> obtenerLockersPorRangoDeCoordenadas(double v, double v1, double v2, double v3);

    List<Locker> encontrarLockersPorCercania(double latitude, double longitude, double maxDistance);

}
