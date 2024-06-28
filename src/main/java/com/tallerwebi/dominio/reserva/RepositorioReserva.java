package com.tallerwebi.dominio.reserva;

import com.tallerwebi.dominio.usuario.Usuario;
import com.tallerwebi.dominio.locker.Locker;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RepositorioReserva {

    Usuario buscarLockerPorUsuario(String email, String password);
    void guardarUsuario(Usuario usuario);
    Usuario buscar(String email);
    void modificar(Usuario usuario);
    void guardarReserva(Reserva reserva);

    Reserva obtenerReservaPorId(Long id);

    void actualizarReserva(Reserva reserva);

    void eliminarReserva(Long id);

    @Transactional
    boolean tieneReservaActiva(Long idUsuario, Long idLocker);

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    List<Locker> obtenerLockersPorIdUsuario(Long idUsuario);
}

