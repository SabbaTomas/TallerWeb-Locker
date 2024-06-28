package com.tallerwebi.integracion;

import com.tallerwebi.dominio.locker.Locker;
import com.tallerwebi.dominio.excepcion.ParametrosDelLockerInvalidos;
import com.tallerwebi.dominio.excepcion.PasswordInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.dominio.locker.RepositorioDatosLocker;
import com.tallerwebi.dominio.usuario.RepositorioUsuario;
import com.tallerwebi.dominio.usuario.ServicioUsuario;
import com.tallerwebi.dominio.usuario.Usuario;
import com.tallerwebi.dominio.locker.ServicioLocker;
import com.tallerwebi.dominio.locker.TipoLocker;
import com.tallerwebi.dominio.reserva.RepositorioReserva;
import com.tallerwebi.infraestructura.usuario.ServicioUsuarioImpl;
import com.tallerwebi.presentacion.ControladorUsuario;
import com.tallerwebi.util.MD5Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServicioUsuarioTest {

    private ServicioUsuario servicioUsuario;
    private RepositorioUsuario repositorioUsuarioMock;
    private ServicioLocker servicioLockerMock;
    private ControladorUsuario controladorUsuarioMock;
    private RepositorioDatosLocker repositorioDatosLockerMock;
    private HttpSession httpSessionMock;

    @BeforeEach
    public void setUp() {
        repositorioUsuarioMock = mock(RepositorioUsuario.class);
        servicioLockerMock = mock(ServicioLocker.class);
        RepositorioReserva repositorioReservaMock = mock(RepositorioReserva.class);
        httpSessionMock = mock(HttpSession.class);

        servicioUsuario = new ServicioUsuarioImpl(repositorioUsuarioMock, servicioLockerMock, repositorioReservaMock);
        controladorUsuarioMock = new ControladorUsuario(servicioUsuario, httpSessionMock);
    }


    @Test
    public void queElUsuarioPuedaObtenerLockersPorSuCodigoPostal() throws ParametrosDelLockerInvalidos {
        // preparación
        String codigoPostal = "1704";

        Usuario usuarioMock = new Usuario();
        usuarioMock.setEmail("nuevo@unlam.com");
        String passwordPlana = "1234";
        String hashedPassword = MD5Util.hash(passwordPlana);
        usuarioMock.setPassword(hashedPassword);
        usuarioMock.setCodigoPostal(codigoPostal);
        usuarioMock.setLatitud(40.7128);
        usuarioMock.setLongitud(74.0060);

        List<Locker> lockers = Arrays.asList(
                new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, codigoPostal),
                new Locker(TipoLocker.MEDIANO, 40.7129, -74.0061, codigoPostal)
        );

        when(repositorioUsuarioMock.buscarUsuarioPorCodigoPostal(codigoPostal)).thenReturn(usuarioMock);
        when(servicioLockerMock.obtenerLockersPorCodigoPostal(codigoPostal)).thenReturn(lockers);

        // ejecución
        List<Locker> resultado = servicioUsuario.obtenerLockersPorCodigoPostalUsuario(codigoPostal);

        // verificación
        assertEquals(lockers, resultado);
    }

    @Test
    public void queElUsuarioPuedaVerLosLockersRegistrados() {
        Long usuarioId = 1L;
        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(usuarioId);

        List<Locker> lockers = Arrays.asList(
                new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "1704"),
                new Locker(TipoLocker.MEDIANO, 40.7129, -74.0061, "1704")
        );

        when(httpSessionMock.getAttribute("userId")).thenReturn(usuarioId);
        when(servicioUsuario.obtenerTodosLosLockersRegistrados(usuarioId)).thenReturn(lockers);

        ModelAndView mav = controladorUsuarioMock.verLockersRegistrados();

        assertEquals("todosLosLockers", mav.getViewName());
        assertEquals(lockers, mav.getModel().get("lockers"));
    }


    @Test
    public void buscarUsuarioPorIdDeberiaRetornarUsuarioCorrecto() {
        // preparación
        Long usuarioId = 1L;
        Usuario usuarioMock = new Usuario();
        usuarioMock.setId(usuarioId);
        when(repositorioUsuarioMock.buscarUsuarioPorId(usuarioId)).thenReturn(usuarioMock);

        // ejecución
        Usuario usuarioEncontrado = servicioUsuario.buscarUsuarioPorId(usuarioId);

        // validación
        assertEquals(usuarioMock, usuarioEncontrado);
    }


    @Test
    public void queSePuedaGuardarUsuarioCorrectamente() {
        // preparación
        Usuario usuario = new Usuario();
        usuario.setEmail("test@unlam.com");

        // ejecución
        servicioUsuario.guardarUsuario(usuario);

        // validación
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(repositorioUsuarioMock).guardar(usuarioCaptor.capture());

        Usuario usuarioGuardado = usuarioCaptor.getValue();
        assertEquals("test@unlam.com", usuarioGuardado.getEmail());
    }

    @Test
    public void queSePuedaModificarUsuarioCorrectamente() {
        // preparación
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(1L);
        usuarioExistente.setEmail("usuarioexistente@unlam.com");
        usuarioExistente.setPassword("viejapassword15");
        usuarioExistente.setCodigoPostal("1704");

        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(1L);
        usuarioActualizado.setEmail("nuevocorreo@unlam.com");
        String newPasswordPlana = "NuevaPassword15";
        String newHashedPassword = MD5Util.hash(newPasswordPlana);
        usuarioActualizado.setPassword(newHashedPassword);
        usuarioActualizado.setCodigoPostal("1704");
        when(repositorioUsuarioMock.buscarUsuarioPorId(1L)).thenReturn(usuarioExistente);

        // ejecución
        servicioUsuario.actualizarDatosUsuario(1L, usuarioActualizado);

        // validación
        verify(repositorioUsuarioMock, times(1)).modificar(usuarioActualizado);
    }

    @Test
    public void queSeLanzeExceptionCuandoNoEncuentraUsuarioPorCodigoPostal() {
        // preparación
        String codigoPostalNoExistente = "9999";

        when(repositorioUsuarioMock.buscarUsuarioPorCodigoPostal(codigoPostalNoExistente)).thenReturn(null);

        // verificación
        assertThrows(UsuarioNoEncontrado.class, () -> {
            servicioUsuario.obtenerLockersPorCodigoPostalUsuario(codigoPostalNoExistente);
        });
    }

    @Test
    public void queSeLanzeExceptionAlActualizarDatosConPasswordInvalido() {
        // Preparación
        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setId(1L);
        usuarioExistente.setEmail("usuario@correo.com");
        usuarioExistente.setCodigoPostal("1704");

        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(1L);
        usuarioActualizado.setEmail("usuario@correo.com");
        usuarioActualizado.setPassword("invalidpassword"); // Contraseña no válida

        when(repositorioUsuarioMock.buscarUsuarioPorId(1L)).thenReturn(usuarioExistente);

        // Ejecución y Verificación
        assertThrows(PasswordInvalido.class, () -> {
            servicioUsuario.actualizarDatosUsuario(1L, usuarioActualizado);
        });
        verify(repositorioUsuarioMock, never()).modificar(usuarioActualizado);
    }

}

