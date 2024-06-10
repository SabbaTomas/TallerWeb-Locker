package com.tallerwebi.dominio.locker;


import com.tallerwebi.dominio.Locker;

import java.util.List;

public interface RepositorioDatosLocker  {

    void guardar(Locker locker);

    Locker actualizar(Locker locker);

    void eliminar(Long idLocker);

    Locker obtenerPorId(Long idLocker);


    List<Locker> obtenerLockersPorTipo(TipoLocker tipoLocker);

    List<Locker> obtenerSeleccionados();

    void eliminarTodos();

    List<Locker> obtenerLockersPorCodigoPostal(String codigoPostal);

    List<Locker> obtenerLockersPorRangoDeCoordenadas(double v, double v1, double v2, double v3);
}
