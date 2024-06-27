package com.tallerwebi.dominio.reserva;

import com.tallerwebi.dominio.locker.Locker;
import com.tallerwebi.dominio.usuario.Usuario;
import com.tallerwebi.dominio.excepcion.ReservaActivaExistenteException;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

public interface ServicioReserva {


    Usuario consultarUsuarioDeReserva(String email, String password);

    @Transactional
    Locker buscarLockerPorID(Long idLocker);

    @Transactional
    boolean tieneReservaActiva(Long idUsuario, Long idLocker);

    @Transactional
    Reserva prepararDatosReserva(Long idUsuario, Long idLocker);

    @Transactional
    Reserva registrarReserva(Reserva reservaDatos) throws UsuarioExistente, ReservaActivaExistenteException;

    double calcularCosto(LocalDate fechaInicio, LocalDate fechaFin);

    Usuario consultarLockerDeReserva(Usuario usuario, Locker locker);
}
