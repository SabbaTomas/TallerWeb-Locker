package com.tallerwebi.presentacion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tallerwebi.dominio.locker.Locker;
import com.tallerwebi.dominio.usuario.ServicioUsuario;
import com.tallerwebi.dominio.usuario.Usuario;
import com.tallerwebi.dominio.excepcion.ReservaActivaExistenteException;
import com.tallerwebi.dominio.locker.ServicioLocker;
import com.tallerwebi.dominio.reserva.Reserva;
import com.tallerwebi.dominio.reserva.ServicioReserva;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Controller
public class ControladorReservaTest {

    @Mock
    private ServicioReserva servicioReservaMock;

    @Mock
    private ServicioLocker servicioLockerMock;

    @Mock
    private ServicioUsuario servicioUsuarioMock;

    @Mock
    private Model modelMock;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest requestMock;

    @Mock
    private HttpSession httpSessionMock;

    @InjectMocks
    private ControladorReserva controladorReserva;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        when(requestMock.getSession()).thenReturn(httpSessionMock);
    }

    @Test
    public void testMostrarFormularioReserva_UsuarioNoLogueado() {
        when(httpSessionMock.getAttribute("USUARIO_ID")).thenReturn(null);

        String vista = controladorReserva.mostrarFormularioReserva(1L, modelMock, requestMock);

        assertEquals("redirect:/login", vista);
    }

    @Test
    public void testMostrarFormularioReserva_Exito() {
        Long idUsuario = 1L;
        Long idLocker = 1L;
        Reserva reserva = new Reserva();
        reserva.setUsuario(new Usuario());
        reserva.setLocker(new Locker());

        when(httpSessionMock.getAttribute("USUARIO_ID")).thenReturn(idUsuario);
        when(servicioReservaMock.prepararDatosReserva(anyLong(), anyLong())).thenReturn(reserva);

        String vista = controladorReserva.mostrarFormularioReserva(idLocker, modelMock, requestMock);

        assertEquals("formularioReserva", vista);
        verify(modelMock).addAttribute("reserva", reserva);
        verify(modelMock).addAttribute("usuario", reserva.getUsuario());
        verify(modelMock).addAttribute("locker", reserva.getLocker());
    }

    @Test
    public void testRegistrarReserva_UsuarioNoLogueado() {
        when(httpSessionMock.getAttribute("USUARIO_ID")).thenReturn(null);

        String vista = controladorReserva.registrarReserva("2024-06-01", "2024-06-10", 1L, requestMock, modelMock);

        assertEquals("redirect:/login", vista);
    }

    @Test
    public void testRegistrarReserva_Exito() throws Exception {
        Long idUsuario = 1L;
        Long idLocker = 1L;
        String fechaReserva = "2024-06-01";
        String fechaFinalizacion = "2024-06-10";
        LocalDate fechaInicio = LocalDate.parse(fechaReserva);
        LocalDate fechaFin = LocalDate.parse(fechaFinalizacion);

        Usuario usuario = new Usuario();
        usuario.setId(idUsuario);
        Locker locker = new Locker();
        locker.setId(idLocker);
        Reserva reserva = new Reserva();
        reserva.setFechaReserva(fechaInicio);
        reserva.setFechaFinalizacion(fechaFin);
        reserva.setLocker(locker);
        reserva.setUsuario(usuario);
        reserva.setCosto(100.0);

        when(httpSessionMock.getAttribute("USUARIO_ID")).thenReturn(idUsuario);
        when(servicioUsuarioMock.buscarUsuarioPorId(anyLong())).thenReturn(usuario);
        when(servicioLockerMock.obtenerLockerPorId(anyLong())).thenReturn(locker);
        when(servicioReservaMock.registrarReserva(any(Reserva.class))).thenReturn(reserva);
        when(objectMapper.writeValueAsString(any(Reserva.class))).thenReturn("{\"json\":\"value\"}");

        String vista = controladorReserva.registrarReserva(fechaReserva, fechaFinalizacion, idLocker, requestMock, modelMock);

        assertEquals("resultadoReserva", vista);
        verify(modelMock).addAttribute("mensaje", "Reserva registrada exitosamente");
        verify(modelMock).addAttribute("reservaId", reserva.getId());
        verify(modelMock).addAttribute("lockerId", idLocker);
        verify(modelMock).addAttribute("costoTotal", reserva.getCosto());
        verify(modelMock).addAttribute("usuarioId", idUsuario);
        verify(modelMock).addAttribute("cantidadLockers", 1);
        verify(modelMock).addAttribute("fechaInicio", fechaInicio);
        verify(modelMock).addAttribute("fechaFin", fechaFin);
        verify(modelMock).addAttribute("jsonReserva", "{\"json\":\"value\"}");
    }

    @Test
    public void testRegistrarReserva_ReservaActivaExistente() throws Exception {
        Long idUsuario = 1L;
        Long idLocker = 1L;
        String fechaReserva = "2024-06-01";
        String fechaFinalizacion = "2024-06-10";

        Usuario usuario = new Usuario();
        usuario.setId(idUsuario);
        Locker locker = new Locker();
        locker.setId(idLocker);

        when(httpSessionMock.getAttribute("USUARIO_ID")).thenReturn(idUsuario);
        when(servicioUsuarioMock.buscarUsuarioPorId(anyLong())).thenReturn(usuario);
        when(servicioLockerMock.obtenerLockerPorId(anyLong())).thenReturn(locker);
        when(servicioReservaMock.tieneReservaActiva(anyLong(), anyLong())).thenReturn(true);

        doThrow(new ReservaActivaExistenteException("El usuario ya tiene una reserva activa para este locker")).when(servicioReservaMock).registrarReserva(any(Reserva.class));

        String vista = controladorReserva.registrarReserva(fechaReserva, fechaFinalizacion, idLocker, requestMock, modelMock);

        assertEquals("resultadoReserva", vista);
        verify(modelMock).addAttribute("mensaje", "El usuario ya tiene una reserva activa para este locker.");
    }




    @Test
    public void testRegistrarReserva_ErrorGeneral() throws Exception {
        Long idUsuario = 1L;
        Long idLocker = 1L;
        String fechaReserva = "2024-06-01";
        String fechaFinalizacion = "2024-06-10";

        Usuario usuario = new Usuario();
        usuario.setId(idUsuario);
        Locker locker = new Locker();
        locker.setId(idLocker);

        when(httpSessionMock.getAttribute("USUARIO_ID")).thenReturn(idUsuario);
        when(servicioUsuarioMock.buscarUsuarioPorId(anyLong())).thenReturn(usuario);
        when(servicioLockerMock.obtenerLockerPorId(anyLong())).thenReturn(locker);
        when(servicioReservaMock.tieneReservaActiva(anyLong(), anyLong())).thenReturn(false);

        doThrow(new RuntimeException("Error inesperado")).when(servicioReservaMock).registrarReserva(any(Reserva.class));

        String vista = controladorReserva.registrarReserva(fechaReserva, fechaFinalizacion, idLocker, requestMock, modelMock);

        assertEquals("resultadoReserva", vista);
        verify(modelMock).addAttribute("mensaje", "Ocurrió un error al registrar la reserva. Error: Error inesperado");
    }


    @Test
    public void testMostrarFormularioReserva_Error() {
        Long idLocker = 1L;
        Long idUsuario = 1L;

        when(httpSessionMock.getAttribute("USUARIO_ID")).thenReturn(idUsuario);
        when(servicioReservaMock.prepararDatosReserva(anyLong(), anyLong())).thenThrow(new IllegalArgumentException("Error de argumento"));

        String vista = controladorReserva.mostrarFormularioReserva(idLocker, modelMock, requestMock);

        assertEquals("error", vista);
        verify(modelMock).addAttribute("mensaje", "Error de argumento");
    }

    @Test
    public void testRegistrarReserva_UsuarioExistente() throws Exception {
        Long idUsuario = 1L;
        Long idLocker = 1L;
        String fechaReserva = "2024-06-01";
        String fechaFinalizacion = "2024-06-10";

        Usuario usuario = new Usuario();
        usuario.setId(idUsuario);
        Locker locker = new Locker();
        locker.setId(idLocker);

        when(httpSessionMock.getAttribute("USUARIO_ID")).thenReturn(idUsuario);
        when(servicioUsuarioMock.buscarUsuarioPorId(anyLong())).thenReturn(usuario);
        when(servicioLockerMock.obtenerLockerPorId(anyLong())).thenReturn(locker);
        when(servicioReservaMock.tieneReservaActiva(anyLong(), anyLong())).thenReturn(true);

        Reserva reserva = new Reserva();
        reserva.setId(1L);
        when(servicioReservaMock.registrarReserva(any(Reserva.class))).thenReturn(reserva);

        String vista = controladorReserva.registrarReserva(fechaReserva, fechaFinalizacion, idLocker, requestMock, modelMock);

        verify(modelMock).addAttribute("mensaje", "Reserva registrada exitosamente");
    }


    @Test
    public void testRegistrarReserva_ErrorAlRegistrar() throws Exception {
        Long idUsuario = 1L;
        Long idLocker = 1L;
        String fechaReserva = "2024-06-01";
        String fechaFinalizacion = "2024-06-10";
        LocalDate fechaInicio = LocalDate.parse(fechaReserva);
        LocalDate fechaFin = LocalDate.parse(fechaFinalizacion);

        Usuario usuario = new Usuario();
        usuario.setId(idUsuario);
        Locker locker = new Locker();
        locker.setId(idLocker);

        when(httpSessionMock.getAttribute("USUARIO_ID")).thenReturn(idUsuario);
        when(servicioUsuarioMock.buscarUsuarioPorId(anyLong())).thenReturn(usuario);
        when(servicioLockerMock.obtenerLockerPorId(anyLong())).thenReturn(locker);
        when(servicioReservaMock.tieneReservaActiva(anyLong(), anyLong())).thenReturn(false);
        doThrow(new RuntimeException("Simulated Exception")).when(servicioReservaMock).registrarReserva(any(Reserva.class));

        String vista = controladorReserva.registrarReserva(fechaReserva, fechaFinalizacion, idLocker, requestMock, modelMock);

        assertEquals("resultadoReserva", vista);
        verify(modelMock).addAttribute("mensaje", "Ocurrió un error al registrar la reserva. Error: Simulated Exception");
    }


    @Test
    void testMostrarFormularioReserva() {
        Long idUsuario = 1L;
        Long idLocker = 1L;

        Usuario usuario = new Usuario();
        usuario.setId(idUsuario);
        Locker locker = new Locker();
        locker.setId(idLocker);
        Reserva reserva = new Reserva();
        reserva.setUsuario(usuario);
        reserva.setLocker(locker);

        when(httpSessionMock.getAttribute("USUARIO_ID")).thenReturn(idUsuario);
        when(servicioReservaMock.prepararDatosReserva(idUsuario, idLocker)).thenReturn(reserva);

        String viewName = controladorReserva.mostrarFormularioReserva(idLocker, modelMock, requestMock);

        assertEquals("formularioReserva", viewName);
        verify(modelMock, times(1)).addAttribute("reserva", reserva);
        verify(modelMock, times(1)).addAttribute("usuario", usuario);
        verify(modelMock, times(1)).addAttribute("locker", locker);
    }

    @Test
    public void testRegistrarReserva() throws Exception {
        Long idUsuario = 1L;
        Long idLocker = 1L;
        String fechaReserva = "2024-06-01";
        String fechaFinalizacion = "2024-06-10";
        LocalDate fechaInicio = LocalDate.parse(fechaReserva);
        LocalDate fechaFin = LocalDate.parse(fechaFinalizacion);

        Usuario usuario = new Usuario();
        usuario.setId(idUsuario);
        Locker locker = new Locker();
        locker.setId(idLocker);

        when(httpSessionMock.getAttribute("USUARIO_ID")).thenReturn(idUsuario);
        when(servicioUsuarioMock.buscarUsuarioPorId(anyLong())).thenReturn(usuario);
        when(servicioLockerMock.obtenerLockerPorId(anyLong())).thenReturn(locker);
        when(servicioReservaMock.tieneReservaActiva(anyLong(), anyLong())).thenReturn(false);
        when(servicioReservaMock.calcularCosto(any(LocalDate.class), any(LocalDate.class))).thenReturn(100.0);

        Reserva reserva = new Reserva();
        reserva.setFechaReserva(fechaInicio);
        reserva.setFechaFinalizacion(fechaFin);
        reserva.setLocker(locker);
        reserva.setUsuario(usuario);
        reserva.setCosto(100.0);

        when(servicioReservaMock.registrarReserva(any(Reserva.class))).thenReturn(reserva);
        when(objectMapper.writeValueAsString(any(Reserva.class))).thenReturn("{\"json\":\"value\"}");

        String viewName = controladorReserva.registrarReserva(fechaReserva, fechaFinalizacion, idLocker, requestMock, modelMock);

        assertEquals("resultadoReserva", viewName);
        verify(modelMock).addAttribute("mensaje", "Reserva registrada exitosamente");
        verify(modelMock).addAttribute("lockerId", idLocker);
        verify(modelMock).addAttribute("costoTotal", 100.0);
        verify(modelMock).addAttribute("usuarioId", idUsuario);
        verify(modelMock).addAttribute("cantidadLockers", 1);
        verify(modelMock).addAttribute("fechaInicio", fechaInicio);
        verify(modelMock).addAttribute("fechaFin", fechaFin);
        verify(modelMock).addAttribute("jsonReserva", "{\"json\":\"value\"}");
    }

}
