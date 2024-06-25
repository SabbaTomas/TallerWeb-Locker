package dominio;

import dominio.excepcion.LockerNoEncontrado;
import dominio.excepcion.ReservaActivaExistenteException;
import dominio.excepcion.UsuarioNoEncontradoException;
import dominio.locker.RepositorioDatosLocker;
import dominio.locker.RepositorioUsuario;
import dominio.reserva.RepositorioReserva;
import dominio.reserva.Reserva;
import dominio.reserva.ServicioReserva;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class ServicioReservaImpl implements ServicioReserva {

    @Autowired
    private RepositorioUsuario repositorioUsuario;

    @Autowired
    private RepositorioDatosLocker repositorioDatosLocker;

    private static final Logger logger = LoggerFactory.getLogger(ServicioReservaImpl.class);

    @Autowired
    private RepositorioReserva repositorioReserva;

    @Override
    @Transactional
    public Usuario consultarUsuarioDeReserva(String email, String password) {
        Usuario usuario = repositorioUsuario.buscarUsuario(email, password);
        if (usuario == null) {
            throw new UsuarioNoEncontradoException("Usuario no encontrado con email: " + email);
        }
        return usuario;
    }

    @Override
    @Transactional
    public Locker buscarLockerPorID(Long idLocker) {
        Locker locker = repositorioDatosLocker.obtenerLockerPorId(idLocker);
        if (locker == null) {
            throw new LockerNoEncontrado("Locker no encontrado con ID: " + idLocker);
        }
        return locker;
    }

    @Override
    @Transactional
    public boolean tieneReservaActiva(Long idUsuario, Long idLocker) {
        return repositorioReserva.tieneReservaActiva(idUsuario, idLocker);
    }

    @Override
    @Transactional
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
    @Transactional
    public Reserva registrarReserva(Reserva reserva) throws ReservaActivaExistenteException {
        try {
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

            repositorioReserva.guardarReserva(reserva);

            return reserva;
        } catch (UsuarioNoEncontradoException | LockerNoEncontrado | ReservaActivaExistenteException e) {
            logger.error("Error al registrar la reserva: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al registrar la reserva", e);
            throw new RuntimeException("Error inesperado al registrar la reserva", e); // Lanzamos una excepción genérica
        }
    }



    public double calcularCosto(LocalDate fechaInicio, LocalDate fechaFin) {
        long diff = ChronoUnit.DAYS.between(fechaInicio, fechaFin);
        return diff * 100;
    }

    @Override
    @Transactional
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

}
