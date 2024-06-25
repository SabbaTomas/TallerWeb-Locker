package dominio.reserva;

import dominio.Usuario;
import org.springframework.transaction.annotation.Transactional;

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
}

