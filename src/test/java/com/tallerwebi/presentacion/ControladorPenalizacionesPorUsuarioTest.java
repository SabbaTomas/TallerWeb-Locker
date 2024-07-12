package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.locker.Locker;
import com.tallerwebi.dominio.penalizacion.Penalizacion;
import com.tallerwebi.dominio.penalizacion.PenalizacionService;
import com.tallerwebi.dominio.reserva.Reserva;
import com.tallerwebi.dominio.reserva.ServicioReserva;
import com.tallerwebi.dominio.usuario.ServicioUsuario;
import com.tallerwebi.dominio.usuario.Usuario;
import com.tallerwebi.dominio.usuario.excepciones.UsuarioNoEncontrado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ControladorPenalizacionesPorUsuarioTest {

    private ServicioUsuario userService;
    private ServicioReserva reservaService;
    private PenalizacionService penalizacionService;
    private HttpSession httpSession;
    private ControladorPenalizacionesPorUsuario controlador;

    @BeforeEach
    public void setUp() {
        userService = Mockito.mock(ServicioUsuario.class);
        reservaService = Mockito.mock(ServicioReserva.class);
        penalizacionService = Mockito.mock(PenalizacionService.class);
        httpSession = Mockito.mock(HttpSession.class);
        controlador = new ControladorPenalizacionesPorUsuario(userService, reservaService, penalizacionService, httpSession);
    }

    @Test
    public void dadoQueBuscarLockersPorUsuarioEntoncesDebeDevolverLockersUsuario() throws UsuarioNoEncontrado {
        Long idUsuario = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(idUsuario);
        List<Reserva> reservas = Arrays.asList(new Reserva(), new Reserva());
        List<Locker> lockers = Arrays.asList(new Locker(), new Locker());
        List<Penalizacion> penalizaciones = Arrays.asList(new Penalizacion(), new Penalizacion());
        List<Object[]> montosPorReserva = Arrays.asList(new Object[]{1L, 100.0, 50.0}, new Object[]{2L, 200.0, 75.0});

        when(httpSession.getAttribute("USUARIO_ID")).thenReturn(idUsuario);
        when(userService.buscarUsuarioPorId(idUsuario)).thenReturn(usuario);
        when(reservaService.obtenerReservasPorUsuario(idUsuario)).thenReturn(reservas);
        when(penalizacionService.obtenerPenalizacionesPorUsuario(idUsuario)).thenReturn(penalizaciones);
        when(penalizacionService.MontosPorUsuario(idUsuario)).thenReturn(montosPorReserva);

        ModelAndView mav = controlador.buscarLockersPorUsuario();

        assertEquals("lockers-usuario", mav.getViewName());
        assertEquals(usuario, mav.getModel().get("usuario"));
        assertEquals(reservas, mav.getModel().get("reservas"));
        assertEquals(penalizaciones, mav.getModel().get("penalizaciones"));
        assertTrue(mav.getModel().containsKey("montoTotalPorReserva"));
        assertTrue(mav.getModel().containsKey("reservaIds"));
    }

    @Test
    public void dadoQueBuscarLockersPorUsuarioCuandoIdUsuarioNoEncontradoEntoncesDebeDevolverError() {
        when(httpSession.getAttribute("USUARIO_ID")).thenReturn(null);

        ModelAndView mav = controlador.buscarLockersPorUsuario();

        assertEquals("error", mav.getViewName());
        assertEquals("ID de usuario no encontrado en la sesión", mav.getModel().get("mensaje"));
    }

    @Test
    public void dadoQueBuscarLockersPorUsuarioCuandoUsuarioNoEncontradoEntoncesDebeDevolverError() throws UsuarioNoEncontrado {
        Long idUsuario = 1L;

        when(httpSession.getAttribute("USUARIO_ID")).thenReturn(idUsuario);
        when(userService.buscarUsuarioPorId(idUsuario)).thenThrow(new UsuarioNoEncontrado("Usuario no encontrado"));

        ModelAndView mav = controlador.buscarLockersPorUsuario();

        assertEquals("error", mav.getViewName());
        assertEquals("Usuario no encontrado", mav.getModel().get("mensaje"));
    }
}
