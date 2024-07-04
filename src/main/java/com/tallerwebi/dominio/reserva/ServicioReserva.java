package com.tallerwebi.dominio.reserva;

import com.tallerwebi.dominio.locker.Locker;
import com.tallerwebi.dominio.usuario.Usuario;
import com.tallerwebi.dominio.reserva.excepciones.ReservaActivaExistenteException;
import com.tallerwebi.dominio.usuario.excepciones.UsuarioExistente;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Transactional
public interface ServicioReserva {

    Usuario consultarUsuarioDeReserva(String email, String password);

    Locker buscarLockerPorID(Long idLocker);

    boolean tieneReservaActiva(Long idUsuario, Long idLocker);

    Reserva prepararDatosReserva(Long idUsuario, Long idLocker);

    Reserva registrarReserva(Reserva reservaDatos) throws UsuarioExistente, ReservaActivaExistenteException;

    double calcularCosto(LocalDate fechaInicio, LocalDate fechaFin);

    Usuario consultarLockerDeReserva(Usuario usuario, Locker locker);
}
