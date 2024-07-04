package com.tallerwebi.integracion;

import com.tallerwebi.dominio.locker.Locker;
import com.tallerwebi.dominio.locker.RepositorioDatosLocker;
import com.tallerwebi.dominio.locker.excepciones.LockerNoEncontrado;
import com.tallerwebi.dominio.reserva.RepositorioReserva;
import com.tallerwebi.dominio.reserva.Reserva;
import com.tallerwebi.dominio.reserva.ServicioReservaImpl;
import com.tallerwebi.dominio.reserva.excepciones.ReservaActivaExistenteException;
import com.tallerwebi.dominio.usuario.RepositorioUsuario;
import com.tallerwebi.dominio.usuario.Usuario;
import com.tallerwebi.dominio.usuario.excepciones.UsuarioNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

    // Test for consultarUsuarioDeReserva
    @Test
    void dadoQueElUsuarioExisteEntoncesConsultarUsuarioDeReservaDebeRetornarUsuario() {
        String email = "test@example.com";
        String password = "password";
        Usuario usuario = new Usuario();
        when(repositorioUsuario.buscarUsuario(email, password)).thenReturn(usuario);

        Usuario result = servicioReserva.consultarUsuarioDeReserva(email, password);

        assertEquals(usuario, result);
        verify(repositorioUsuario, times(1)).buscarUsuario(email, password);
    }

    // Test for consultarUsuarioDeReserva when user is not found
    @Test
    void dadoQueElUsuarioNoExisteEntoncesConsultarUsuarioDeReservaDebeLanzarExcepcion() {
        String email = "test@example.com";
        String password = "password";
        when(repositorioUsuario.buscarUsuario(email, password)).thenReturn(null);

        assertThrows(UsuarioNoEncontradoException.class, () -> servicioReserva.consultarUsuarioDeReserva(email, password));
    }

    // Test for buscarLockerPorID
    @Test
    void dadoQueElLockerExisteEntoncesBuscarLockerPorIDDebeRetornarLocker() {
        Long idLocker = 1L;
        Locker locker = new Locker();
        locker.setId(idLocker);
        when(repositorioDatosLocker.obtenerLockerPorId(idLocker)).thenReturn(locker);

        Locker result = servicioReserva.buscarLockerPorID(idLocker);

        assertEquals(locker, result);
        verify(repositorioDatosLocker, times(1)).obtenerLockerPorId(idLocker);
    }

    // Test for buscarLockerPorID when locker is not found
    @Test
    void dadoQueElLockerNoExisteEntoncesBuscarLockerPorIDDebeLanzarExcepcion() {
        Long idLocker = 1L;
        when(repositorioDatosLocker.obtenerLockerPorId(idLocker)).thenReturn(null);

        assertThrows(LockerNoEncontrado.class, () -> servicioReserva.buscarLockerPorID(idLocker));
    }

    // Test for tieneReservaActiva
    @Test
    void dadoQueExisteReservaActivaEntoncesTieneReservaActivaDebeRetornarTrue() {
        Long idUsuario = 1L;
        Long idLocker = 1L;
        when(repositorioReserva.tieneReservaActiva(idUsuario, idLocker)).thenReturn(true);

        boolean result = servicioReserva.tieneReservaActiva(idUsuario, idLocker);

        assertTrue(result);
        verify(repositorioReserva, times(1)).tieneReservaActiva(idUsuario, idLocker);
    }

    // Test for tieneReservaActiva when there is no active reservation
    @Test
    void dadoQueNoExisteReservaActivaEntoncesTieneReservaActivaDebeRetornarFalse() {
        Long idUsuario = 1L;
        Long idLocker = 1L;
        when(repositorioReserva.tieneReservaActiva(idUsuario, idLocker)).thenReturn(false);

        boolean result = servicioReserva.tieneReservaActiva(idUsuario, idLocker);

        assertFalse(result);
        verify(repositorioReserva, times(1)).tieneReservaActiva(idUsuario, idLocker);
    }

    // Test for prepararDatosReserva when user is not found
    @Test
    void dadoQueElUsuarioNoExisteEntoncesPrepararDatosReservaDebeLanzarExcepcion() {
        Long idUsuario = 1L;
        Long idLocker = 1L;
        when(repositorioUsuario.existeUsuarioPorId(idUsuario)).thenReturn(false);

        assertThrows(UsuarioNoEncontradoException.class, () -> servicioReserva.prepararDatosReserva(idUsuario, idLocker));
    }

    // Test for prepararDatosReserva when locker is not found
    @Test
    void dadoQueElLockerNoExisteEntoncesPrepararDatosReservaDebeLanzarExcepcion() {
        Long idUsuario = 1L;
        Long idLocker = 1L;
        when(repositorioUsuario.existeUsuarioPorId(idUsuario)).thenReturn(true);
        when(repositorioDatosLocker.obtenerLockerPorId(idLocker)).thenThrow(new LockerNoEncontrado("Locker no encontrado"));

        assertThrows(LockerNoEncontrado.class, () -> servicioReserva.prepararDatosReserva(idUsuario, idLocker));
    }

    // Test for prepararDatosReserva
    @Test
    void dadoQueUsuarioYLockerExistenEntoncesPrepararDatosReservaDebeRetornarReserva() {
        Long idUsuario = 1L;
        Long idLocker = 1L;
        Usuario usuario = new Usuario();
        Locker locker = new Locker();
        when(repositorioUsuario.existeUsuarioPorId(idUsuario)).thenReturn(true);
        when(repositorioDatosLocker.obtenerLockerPorId(idLocker)).thenReturn(locker);
        when(repositorioUsuario.buscarUsuarioPorId(idUsuario)).thenReturn(usuario);

        Reserva result = servicioReserva.prepararDatosReserva(idUsuario, idLocker);

        assertNotNull(result);
        assertEquals(usuario, result.getUsuario());
        assertEquals(locker, result.getLocker());
    }

    // Test for registrarReserva when user is not found
    @Test
    void dadoQueElUsuarioNoExisteEntoncesRegistrarReservaDebeLanzarExcepcion() {
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

        when(repositorioUsuario.buscarUsuario(usuario.getEmail(), usuario.getPassword())).thenReturn(null);

        assertThrows(UsuarioNoEncontradoException.class, () -> servicioReserva.registrarReserva(reserva));
    }

    // Test for registrarReserva when user has active reservation
    @Test
    void dadoQueElUsuarioTieneReservaActivaEntoncesRegistrarReservaDebeLanzarExcepcion() {
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
        verify(repositorioReserva, never()).guardarReserva(any(Reserva.class));
    }

    // Test for registrarReserva
    @Test
    void dadoQueUsuarioYLockerExistenYNoHayReservaActivaEntoncesRegistrarReservaDebeGuardarReserva() throws ReservaActivaExistenteException, UsuarioNoEncontradoException, LockerNoEncontrado {
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
        verify(repositorioReserva, times(1)).guardarReserva(any(Reserva.class));
        assertEquals(servicioReserva.calcularCosto(fechaInicio, fechaFin), result.getCosto(), 0.01);
    }

    // Test for calcularCosto
    @Test
    void dadoQueSeCalculaElCostoEntoncesDebeRetornarElCostoCorrecto() {
        LocalDate fechaInicio = LocalDate.of(2024, 6, 1);
        LocalDate fechaFin = LocalDate.of(2024, 6, 10);
        double expectedCost = 900.0;

        double result = servicioReserva.calcularCosto(fechaInicio, fechaFin);

        assertEquals(expectedCost, result, 0.01);
    }


}




