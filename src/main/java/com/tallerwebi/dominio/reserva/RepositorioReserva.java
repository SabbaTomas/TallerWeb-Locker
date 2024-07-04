package com.tallerwebi.dominio.reserva;

import com.tallerwebi.dominio.usuario.Usuario;
import com.tallerwebi.dominio.locker.Locker;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface RepositorioReserva  {

    Usuario buscarLockerPorUsuario(String email, String password);
    void guardarUsuario(Usuario usuario);
    Usuario buscar(String email);
    void modificar(Usuario usuario);
    void guardarReserva(Reserva reserva);

    Reserva obtenerReservaPorId(Long id);

    @SuppressWarnings("unchecked")
    List<Reserva> findAll();

    void actualizarReserva(Reserva reserva);

    void eliminarReserva(Long id);

    boolean tieneReservaActiva(Long idUsuario, Long idLocker);

    List<Locker> obtenerLockersPorIdUsuario(Long idUsuario);

    List<Reserva> obtenerReservasPorUsuario(Long idUsuario);
}

