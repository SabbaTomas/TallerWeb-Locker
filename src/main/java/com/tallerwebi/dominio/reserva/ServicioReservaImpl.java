package com.tallerwebi.dominio.reserva;

import com.tallerwebi.dominio.penalizacion.PenalizacionService;
import com.tallerwebi.dominio.reserva.excepciones.ReservaActivaExistenteException;
import com.tallerwebi.dominio.reserva.excepciones.ReservaNoEncontradaException;
import com.tallerwebi.dominio.usuario.excepciones.UsuarioNoEncontradoException;
import com.tallerwebi.dominio.locker.Locker;
import com.tallerwebi.dominio.locker.RepositorioDatosLocker;
import com.tallerwebi.dominio.locker.excepciones.LockerNoEncontrado;
import com.tallerwebi.dominio.usuario.RepositorioUsuario;
import com.tallerwebi.dominio.usuario.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ServicioReservaImpl implements ServicioReserva {


    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioDatosLocker repositorioDatosLocker;
    private final RepositorioReserva repositorioReserva;
    private final PenalizacionService penalizacionService;


    @Autowired
    public ServicioReservaImpl(RepositorioUsuario repositorioUsuario, RepositorioDatosLocker repositorioDatosLocker, RepositorioReserva repositorioReserva, PenalizacionService penalizacionService) {
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioDatosLocker = repositorioDatosLocker;
        this.repositorioReserva = repositorioReserva;
        this.penalizacionService = penalizacionService;
    }


    @Override
    public Usuario consultarUsuarioDeReserva(String email, String password) {
        Usuario usuario = repositorioUsuario.buscarUsuario(email, password);
        if (usuario == null) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado con email: " + email);
        }
        return usuario;
    }

    @Override
    public boolean tieneReservaActiva(Long idUsuario, Long idLocker) {
        return repositorioReserva.tieneReservaActiva(idUsuario, idLocker);
    }

    @Override
    public Reserva prepararDatosReserva(Long idUsuario, Long idLocker) {
        if (!repositorioUsuario.existeUsuarioPorId(idUsuario)) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + idUsuario);
        }

        Locker locker = repositorioDatosLocker.obtenerLockerPorId(idLocker);
        if (locker == null) {
            throw new LockerNoEncontrado("Locker no encontrado con ID: " + idLocker);
        }

        Usuario usuario = repositorioUsuario.buscarUsuarioPorId(idUsuario);
        if (usuario == null) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado con ID: " + idUsuario);
        }

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setLocker(locker);

        return reserva;
    }

    @Override
    public Reserva registrarReserva(Reserva reserva) throws ReservaActivaExistenteException, UsuarioNoEncontradoException, LockerNoEncontrado {
        Usuario usuario = repositorioUsuario.buscarUsuario(reserva.getUsuario().getEmail(), reserva.getUsuario().getPassword());
        if (usuario == null) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado con email: " + reserva.getUsuario().getEmail());
        }

        if (repositorioReserva.tieneReservaActiva(usuario.getId(), reserva.getLocker().getId())) {
            throw new ReservaActivaExistenteException("El usuario ya tiene una reserva activa para este locker");
        }

        Locker locker = repositorioDatosLocker.obtenerLockerPorId(reserva.getLocker().getId());
        if (locker == null) {
            throw new LockerNoEncontrado("Locker no encontrado con ID: " + reserva.getLocker().getId());
        }

        double costoTotal = calcularCosto(reserva.getFechaReserva(), reserva.getFechaFinalizacion());

        reserva.setCosto(costoTotal);
        reserva.setEstado("pendiente");

        repositorioReserva.guardarReserva(reserva);

        return reserva;
    }

    public double calcularCosto(LocalDate fechaInicio, LocalDate fechaFin) {
        long diff = ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        return diff * 100;
    }

    @Override
    public Usuario consultarLockerDeReserva(Usuario usuario, Locker locker) {
        Locker lockerExistente = repositorioDatosLocker.obtenerLockerPorId(locker.getId());
        Usuario usuarioExistente = repositorioUsuario.buscar(usuario.getEmail());

        if (lockerExistente == null) {
            throw new LockerNoEncontrado("Locker no encontrado con ID: " + locker.getId());
        }

        if (usuarioExistente == null) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado con email: " + usuario.getEmail());
        }

        return usuarioExistente;
    }

    public Reserva finalizarReserva(Long idReserva) {
        Reserva reserva = repositorioReserva.obtenerReservaPorId(idReserva);
        if (reserva != null) {
            double penalizacionPagoTardio = penalizacionService.calcularPenalizacionPorPagoTardio(reserva.getFechaReserva(), LocalDate.now());
            double penalizacionUsoExtendido = penalizacionService.calcularPenalizacionPorUsoExtendido(reserva.getFechaFinalizacion(), LocalDate.now());
            double penalizacionTotal = penalizacionPagoTardio + penalizacionUsoExtendido;

            reserva.setCosto(reserva.getCosto() + penalizacionTotal);
            reserva.setEstado("finalizada");

            repositorioReserva.guardarReserva(reserva);
        }
        return reserva;
    }



    @Override
    public List<Reserva> obtenerReservasPorUsuario(Long id) {
        List<Reserva> reserva = repositorioReserva.obtenerReservasPorUsuario(id);
        if(reserva == null) {
            throw new ReservaNoEncontradaException("No hay una reserva registrada para este usuario" + id);
        }
        return reserva;
    }

    @Override
    public Locker buscarLockerPorID(Long idLocker) {
        Locker locker = repositorioDatosLocker.obtenerLockerPorId(idLocker);
        if (locker == null) {
            throw new LockerNoEncontrado("Locker no encontrado con ID: " + idLocker);
        }
        return locker;
    }
    @Override
    public void verificarYAplicarPenalizaciones() {
        List<Reserva> reservas = repositorioReserva.obtenerTodasLasReservas();
        for (Reserva reserva : reservas) {
            if ("pendiente".equals(reserva.getEstado())) {
                double penalizacionPagoTardio = penalizacionService.calcularPenalizacionPorPagoTardio(reserva.getFechaReserva(), LocalDate.now());
                double penalizacionUsoExtendido = penalizacionService.calcularPenalizacionPorUsoExtendido(reserva.getFechaFinalizacion(), LocalDate.now());
                double penalizacionTotal = penalizacionPagoTardio + penalizacionUsoExtendido;

                if (penalizacionTotal > 0) {
                    reserva.setEstado("penalizado");
                    repositorioReserva.guardarReserva(reserva);
                    penalizacionService.registrarPenalizacion(reserva, penalizacionTotal);
                }

            }
        }
    }

    @Override
    public List<Reserva> getReservas(){
        return repositorioReserva.obtenerTodasLasReservas();
    }

}
