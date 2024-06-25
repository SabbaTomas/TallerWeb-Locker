package dominio.reserva;

import dominio.excepcion.ReservaActivaExistenteException;
import dominio.excepcion.UsuarioExistente;
import dominio.Locker;
import dominio.Usuario;
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
