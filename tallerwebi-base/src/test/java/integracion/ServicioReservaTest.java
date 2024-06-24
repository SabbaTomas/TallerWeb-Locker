package integracion;

import dominio.Locker;
import dominio.ServicioReservaImpl;
import dominio.Usuario;
import dominio.excepcion.LockerNoEncontrado;
import dominio.excepcion.ReservaActivaExistenteException;
import dominio.excepcion.UsuarioExistente;
import dominio.excepcion.UsuarioNoEncontradoException;
import dominio.locker.RepositorioDatosLocker;
import dominio.locker.RepositorioUsuario;
import dominio.reserva.RepositorioReserva;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import dominio.reserva.Reserva;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ServicioReservaTest {


    @Mock
    private RepositorioUsuario repositorioUsuario;

    @Mock
    private RepositorioDatosLocker repositorioDatosLocker;

    @Mock
    private RepositorioReserva repositorioReserva;

    @InjectMocks
    private ServicioReservaImpl servicioReserva;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConsultarUsuarioDeReserva() {
        String email = "test@example.com";
        String password = "password";
        Usuario usuario = new Usuario();
        when(repositorioUsuario.buscarUsuario(email, password)).thenReturn(usuario);

        Usuario result = servicioReserva.consultarUsuarioDeReserva(email, password);

        assertEquals(usuario, result);
        verify(repositorioUsuario, times(1)).buscarUsuario(email, password);
    }

    @Test
    void testConsultarLockerDeReserva() {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");
        Locker locker = new Locker();
        locker.setId(1L);

        when(repositorioDatosLocker.obtenerLockerPorId(locker.getId())).thenReturn(locker);
        when(repositorioUsuario.buscar(usuario.getEmail())).thenReturn(usuario);

        Usuario result = servicioReserva.consultarLockerDeReserva(usuario, locker);

        assertEquals(usuario, result);
        verify(repositorioDatosLocker, times(1)).obtenerLockerPorId(locker.getId());
        verify(repositorioUsuario, times(1)).buscar(usuario.getEmail());
    }

    @Test
    void testPrepararDatosReservaUsuarioNoEncontrado() {
        Long idUsuario = 1L;
        Long idLocker = 1L;

        when(repositorioUsuario.existeUsuarioPorId(idUsuario)).thenReturn(false);

        assertThrows(UsuarioNoEncontradoException.class, () -> servicioReserva.prepararDatosReserva(idUsuario, idLocker));
    }

    @Test
    void testPrepararDatosReservaLockerNoEncontrado() {
        Long idUsuario = 1L;
        Long idLocker = 1L;

        when(repositorioUsuario.existeUsuarioPorId(idUsuario)).thenReturn(true);
        when(repositorioDatosLocker.obtenerLockerPorId(idLocker)).thenThrow(new LockerNoEncontrado("Locker no encontrado"));

        assertThrows(LockerNoEncontrado.class, () -> servicioReserva.prepararDatosReserva(idUsuario, idLocker));
    }

    @Test
    void testRegistrarReservaUsuarioExistente() {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");
        usuario.setPassword("password");

        Locker locker = new Locker();
        locker.setId(1L);

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setLocker(locker);
        reserva.setFechaReserva(LocalDate.of(2024, 6, 1));
        reserva.setFechaFinalizacion(LocalDate.of(2024, 6, 10));

        when(repositorioUsuario.buscarUsuario(usuario.getEmail(), usuario.getPassword())).thenReturn(usuario);
        when(repositorioReserva.tieneReservaActiva(usuario.getId(), locker.getId())).thenReturn(true);

        assertThrows(ReservaActivaExistenteException.class, () -> servicioReserva.registrarReserva(reserva));

        verify(repositorioUsuario, times(1)).buscarUsuario(usuario.getEmail(), usuario.getPassword());
        verify(repositorioDatosLocker, never()).obtenerLockerPorId(anyLong());
        verify(repositorioReserva, never()).guardarReserva(any(dominio.reserva.Reserva.class));
    }

    @Test
    void testBuscarLockerPorID() {
        Long idLocker = 1L;
        Locker locker = new Locker();
        locker.setId(idLocker);

        when(repositorioDatosLocker.obtenerLockerPorId(idLocker)).thenReturn(locker);

        Locker result = servicioReserva.buscarLockerPorID(idLocker);

        assertEquals(locker, result);
        verify(repositorioDatosLocker, times(1)).obtenerLockerPorId(idLocker);
    }

    @Test
    void testTieneReservaActiva() {
        Long idUsuario = 1L;
        Long idLocker = 1L;

        when(repositorioReserva.tieneReservaActiva(idUsuario, idLocker)).thenReturn(true);

        boolean result = servicioReserva.tieneReservaActiva(idUsuario, idLocker);

        assertTrue(result);
        verify(repositorioReserva, times(1)).tieneReservaActiva(idUsuario, idLocker);
    }

    @Test
    void testCalcularCosto() {
        LocalDate fechaInicio = LocalDate.of(2024, 6, 1);
        LocalDate fechaFin = LocalDate.of(2024, 6, 10);
        double expectedCosto = 900.0;

        double result = servicioReserva.calcularCosto(fechaInicio, fechaFin);

        assertEquals(expectedCosto, result, 0.01);
    }

    @Test
    void testRegistrarReserva() throws ReservaActivaExistenteException, UsuarioExistente {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");
        usuario.setPassword("test");

        Locker locker = new Locker();
        locker.setId(1L);

        LocalDate fechaInicio = LocalDate.of(2024, 6, 1);
        LocalDate fechaFin = LocalDate.of(2024, 6, 10);

        when(repositorioUsuario.buscarUsuario(usuario.getEmail(), usuario.getPassword())).thenReturn(usuario);
        when(repositorioDatosLocker.obtenerLockerPorId(locker.getId())).thenReturn(locker);
        when(repositorioReserva.tieneReservaActiva(anyLong(), anyLong())).thenReturn(false);

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setLocker(locker);
        reserva.setFechaReserva(fechaInicio);
        reserva.setFechaFinalizacion(fechaFin);

        Reserva result = servicioReserva.registrarReserva(reserva);

        verify(repositorioUsuario, times(1)).buscarUsuario(usuario.getEmail(), usuario.getPassword());
        verify(repositorioDatosLocker, times(1)).obtenerLockerPorId(locker.getId());
        verify(repositorioReserva, times(1)).guardarReserva(any(dominio.reserva.Reserva.class));
        assertEquals(servicioReserva.calcularCosto(fechaInicio, fechaFin), result.getCosto(), 0.01);
    }

    @Test
    void debeRegistrarReservaConCalculoDeCosto() throws ReservaActivaExistenteException, UsuarioExistente {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");
        usuario.setPassword("password");

        Locker locker = new Locker();
        locker.setId(1L);

        LocalDate fechaInicio = LocalDate.of(2024, 6, 1);
        LocalDate fechaFin = LocalDate.of(2024, 6, 10);

        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setLocker(locker);
        reserva.setFechaReserva(fechaInicio);
        reserva.setFechaFinalizacion(fechaFin);

        when(repositorioUsuario.buscarUsuario(usuario.getEmail(), usuario.getPassword())).thenReturn(usuario);
        when(repositorioReserva.tieneReservaActiva(usuario.getId(), locker.getId())).thenReturn(false);
        when(repositorioDatosLocker.obtenerLockerPorId(locker.getId())).thenReturn(locker);

        servicioReserva.registrarReserva(reserva);

        verify(repositorioUsuario, times(1)).buscarUsuario(usuario.getEmail(), usuario.getPassword());
        verify(repositorioDatosLocker, times(1)).obtenerLockerPorId(locker.getId());
        verify(repositorioReserva, times(1)).guardarReserva(any(dominio.reserva.Reserva.class));

        double costoEsperado = servicioReserva.calcularCosto(fechaInicio, fechaFin);
        assertEquals(costoEsperado, reserva.getCosto(), 0.01);
    }


}
