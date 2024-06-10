package com.tallerwebi.integracion;

import com.tallerwebi.dominio.Locker;
import com.tallerwebi.dominio.reserva.RepositorioReserva;
import com.tallerwebi.dominio.reserva.Reserva;
import com.tallerwebi.dominio.ServicioReservaImpl;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.locker.RepositorioDatosLocker;
import com.tallerwebi.dominio.locker.RepositorioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ServicioReservaImplTest {

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
        when(repositorioDatosLocker.obtenerPorId(locker.getId())).thenReturn(locker);
        when(repositorioUsuario.buscar(usuario.getEmail())).thenReturn(usuario);

        Usuario result = servicioReserva.consultarLockerDeReserva(usuario, locker);

        assertEquals(usuario, result);
        verify(repositorioDatosLocker, times(1)).obtenerPorId(locker.getId());
        verify(repositorioUsuario, times(1)).buscar(usuario.getEmail());
    }

    @Test
    void testRegistrarReserva() throws UsuarioExistente {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");
        Locker locker = new Locker();
        locker.setId(1L);
        when(repositorioUsuario.buscar(usuario.getEmail())).thenReturn(null);
        when(repositorioDatosLocker.obtenerPorId(locker.getId())).thenReturn(locker);

        servicioReserva.registrarReserva(usuario, locker);

        verify(repositorioUsuario, times(1)).buscar(usuario.getEmail());
        verify(repositorioDatosLocker, times(1)).obtenerPorId(locker.getId());
        verify(repositorioReserva, times(1)).guardarReserva(any(Reserva.class));
    }

    @Test
    void testRegistrarReservaUsuarioExistente() {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@example.com");
        Locker locker = new Locker();
        locker.setId(1L);
        when(repositorioUsuario.buscar(usuario.getEmail())).thenReturn(usuario);

        assertThrows(UsuarioExistente.class, () -> servicioReserva.registrarReserva(usuario, locker));

        verify(repositorioUsuario, times(1)).buscar(usuario.getEmail());
        verify(repositorioDatosLocker, never()).obtenerPorId(anyLong());
        verify(repositorioReserva, never()).guardarReserva(any(Reserva.class));
    }
}
