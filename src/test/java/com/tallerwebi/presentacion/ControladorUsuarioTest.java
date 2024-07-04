package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.locker.Locker;
import com.tallerwebi.dominio.reserva.Reserva;
import com.tallerwebi.dominio.usuario.ServicioUsuario;
import com.tallerwebi.dominio.usuario.excepciones.UsuarioNoEncontrado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ControladorUsuarioTest {

    @Mock
    private ServicioUsuario servicioUsuarioMock;

    @Mock
    private HttpSession sessionMock;

    @Mock
    private ControladorUsuario controladorUsuario;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        controladorUsuario = new ControladorUsuario(servicioUsuarioMock, sessionMock);
    }

    @Test
    public void dadoUnUsuarioIdValidoBuscarLockersPorUsuarioDeberiaRetornarLockersYReservas() {
        // Dado
        Long idUsuario = 1L;
        when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(idUsuario);

        Locker lockerMock = new Locker();
        Reserva reservaMock = new Reserva();
        reservaMock.setLocker(lockerMock);

        List<Reserva> reservasMock = List.of(reservaMock);

        when(servicioUsuarioMock.obtenerReservasPorUsuario(idUsuario)).thenReturn(reservasMock);

        // Cuando
        ModelAndView modelAndView = controladorUsuario.buscarLockersPorUsuario();

        // Entonces
        assertEquals("lockers-usuario", modelAndView.getViewName());
        assertEquals(reservasMock, modelAndView.getModel().get("reservas"));
        assertEquals(List.of(lockerMock), modelAndView.getModel().get("lockers"));
    }

    @Test
    public void dadoUnUsuarioIdInvalidoBuscarLockersPorUsuarioDeberiaRetornarError() {
        // Dado
        Long idUsuario = 1L;
        when(sessionMock.getAttribute("USUARIO_ID")).thenReturn(idUsuario);
        when(servicioUsuarioMock.obtenerReservasPorUsuario(idUsuario)).thenThrow(new UsuarioNoEncontrado("Usuario no encontrado"));

        // Cuando
        ModelAndView modelAndView = controladorUsuario.buscarLockersPorUsuario();

        // Entonces
        assertEquals("error", modelAndView.getViewName());
        assertEquals("Usuario no encontrado", modelAndView.getModel().get("mensaje"));
    }

    @Test
    public void dadoUnUsuarioIdValidoVerLockersRegistradosDeberiaRetornarLockers() {
        // Dado
        Long idUsuario = 1L;
        when(sessionMock.getAttribute("userId")).thenReturn(idUsuario);
        List<Locker> lockersMock = List.of(new Locker());
        when(servicioUsuarioMock.obtenerTodosLosLockersRegistrados(idUsuario)).thenReturn(lockersMock);

        // Cuando
        ModelAndView modelAndView = controladorUsuario.verLockersRegistrados();

        // Entonces
        assertEquals("todosLosLockers", modelAndView.getViewName());
        assertEquals(lockersMock, modelAndView.getModel().get("lockers"));
    }

    @Test
    public void dadoUnUsuarioIdInvalidoVerLockersRegistradosDeberiaRetornarError() {
        // Dado
        Long idUsuario = 11L;
        when(sessionMock.getAttribute("userId")).thenReturn(idUsuario);
        when(servicioUsuarioMock.obtenerTodosLosLockersRegistrados(idUsuario)).thenThrow(new UsuarioNoEncontrado("Usuario no encontrado"));

        // Cuando
        ModelAndView modelAndView = controladorUsuario.verLockersRegistrados();

        // Entonces
        assertEquals("error", modelAndView.getViewName());
        assertEquals("Usuario no encontrado", modelAndView.getModel().get("mensaje"));
    }
}
