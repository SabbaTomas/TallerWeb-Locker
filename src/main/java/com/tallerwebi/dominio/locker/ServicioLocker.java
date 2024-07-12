package com.tallerwebi.dominio.locker;

import com.tallerwebi.dominio.locker.Enum.TipoLocker;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface ServicioLocker {

    void crearLocker(Locker locker);
    void actualizarLocker(Long idLocker, TipoLocker tipoLocker);
    void eliminarLocker(Long idLocker);
    void eliminarTodos();
    List<Locker> obtenerLockersPorTipo(TipoLocker tipoLocker);
    Locker obtenerLockerPorId(Long lockerId);

    List<Locker> obtenerLockersSeleccionados();

    List<Locker> obtenerLockersPorCodigoPostal(String codigoPostal);

    List<Locker> obtenerLockersCercanos(double latitud, double longitud, double radio);

    List<Locker> buscarLockers(String codigoPostal, Double latitud, Double longitud, Double radio);

}
