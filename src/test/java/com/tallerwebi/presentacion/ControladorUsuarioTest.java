package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.locker.Locker;
import com.tallerwebi.dominio.usuario.ServicioUsuario;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
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

    private ControladorUsuario controladorUsuario;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        controladorUsuario = new ControladorUsuario(servicioUsuarioMock, sessionMock);
    }

    @Test
    public void buscarLockersPorCodigoPostalUsuarioDeberiaRetornarLockers() {
        // Preparación
        String codigoPostal = "12345";
        when(sessionMock.getAttribute("codigoPostalUsuario")).thenReturn(codigoPostal);
        List<Locker> lockersMock = List.of(new Locker());
        when(servicioUsuarioMock.obtenerLockersPorCodigoPostalUsuario(codigoPostal)).thenReturn(lockersMock);

        // Ejecución
        ModelAndView modelAndView = controladorUsuario.buscarLockersPorCodigoPostalUsuario();

        // Validación
        assertEquals("lockers-usuario", modelAndView.getViewName());
        assertEquals(lockersMock, modelAndView.getModel().get("lockers"));
        assertEquals(codigoPostal, modelAndView.getModel().get("codigoPostal"));
        assertEquals(null, modelAndView.getModel().get("latitud"));
        assertEquals(null, modelAndView.getModel().get("longitud"));
        assertEquals(null, modelAndView.getModel().get("mostrarAlternativos"));
    }

    @Test
    public void buscarLockersPorCodigoPostalUsuarioDeberiaManejarExcepciones() {
        // Preparación
        String codigoPostal = "12345";
        when(sessionMock.getAttribute("codigoPostalUsuario")).thenReturn(codigoPostal);
        when(servicioUsuarioMock.obtenerLockersPorCodigoPostalUsuario(codigoPostal)).thenThrow(new UsuarioNoEncontrado("Usuario no encontrado"));

        // Ejecución
        ModelAndView modelAndView = controladorUsuario.buscarLockersPorCodigoPostalUsuario();

        // Validación
        assertEquals("error", modelAndView.getViewName());
        assertEquals("Usuario no encontrado", modelAndView.getModel().get("mensaje"));
    }

    @Test
    public void verLockersRegistradosDeberiaRetornarLockers() {
        // Preparación
        Long idUsuario = 1L;
        when(sessionMock.getAttribute("userId")).thenReturn(idUsuario);
        List<Locker> lockersMock = List.of(new Locker());
        when(servicioUsuarioMock.obtenerTodosLosLockersRegistrados(idUsuario)).thenReturn(lockersMock);

        // Ejecución
        ModelAndView modelAndView = controladorUsuario.verLockersRegistrados();

        // Validación
        assertEquals("todosLosLockers", modelAndView.getViewName());
        assertEquals(lockersMock, modelAndView.getModel().get("lockers"));
    }

    @Test
    public void verLockersRegistradosDeberiaManejarExcepciones() {
        // Preparación
        Long idUsuario = 1L;
        when(sessionMock.getAttribute("userId")).thenReturn(idUsuario);
        when(servicioUsuarioMock.obtenerTodosLosLockersRegistrados(idUsuario)).thenThrow(new UsuarioNoEncontrado("Usuario no encontrado"));

        // Ejecución
        ModelAndView modelAndView = controladorUsuario.verLockersRegistrados();

        // Validación
        assertEquals("error", modelAndView.getViewName());
        assertEquals("Usuario no encontrado", modelAndView.getModel().get("mensaje"));
    }
}
