package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.locker.Locker;
import com.tallerwebi.dominio.locker.ServicioLocker;
import com.tallerwebi.dominio.reserva.Reserva;
import com.tallerwebi.dominio.reserva.ServicioReserva;
import com.tallerwebi.dominio.reserva.excepciones.ReservaActivaExistenteException;
import com.tallerwebi.dominio.usuario.ServicioUsuario;
import com.tallerwebi.dominio.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    private ServicioReserva servicioReservaMock;
    private ServicioLocker servicioLockerMock;
    private ServicioUsuario servicioUsuarioMock;
    private Model modelMock;
    private HttpServletRequest requestMock;
    private HttpSession httpSessionMock;
    private ControladorReserva controladorReserva;

    @BeforeEach
    public void init() {
        servicioReservaMock = mock(ServicioReserva.class);
        servicioLockerMock = mock(ServicioLocker.class);
        servicioUsuarioMock = mock(ServicioUsuario.class);
        modelMock = mock(Model.class);
        requestMock = mock(HttpServletRequest.class);
        httpSessionMock = mock(HttpSession.class);
        controladorReserva = new ControladorReserva(servicioReservaMock,servicioLockerMock,servicioUsuarioMock);

        when(requestMock.getSession()).thenReturn(httpSessionMock);
    }


    @Test
    public void dadoQueUnUsuarioNoEstaLogeadoEntoncesRedirigirALogin() {
        when(httpSessionMock.getAttribute("USUARIO_ID")).thenReturn(null);

        String vista = controladorReserva.mostrarFormularioReserva(1L, modelMock, requestMock);

        assertEquals("redirect:/login", vista);
    }

    @Test
    public void dadoQueUnUsuarioEstaLogueadoCuandoMostrarFormularioDeReserva() {
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
    public void dadoQueUnUsuarioNoEstaLogueadoCuandoRegistrarReservaEntoncesRedirigirALogin() {
        when(httpSessionMock.getAttribute("USUARIO_ID")).thenReturn(null);

        String vista = controladorReserva.registrarReserva("2024-06-01", "2024-06-10", 1L, requestMock, modelMock);

        assertEquals("redirect:/login", vista);
    }

    @Test
    public void dadoQueDatosValidosCuandoRegistrarReservaEntoncesReservaRegistrada() throws Exception {
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
    }

    @Test
    public void dadoQueReservaExistenteCuandoRegistrarReservaEntoncesMostrarMensajeError() throws Exception {
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
        when(servicioReservaMock.registrarReserva(any(Reserva.class))).thenThrow(new ReservaActivaExistenteException("Reserva activa existente"));

        String vista = controladorReserva.registrarReserva(fechaReserva, fechaFinalizacion, idLocker, requestMock, modelMock);

        assertEquals("resultadoReserva", vista);
        verify(modelMock).addAttribute("mensaje", "Reserva activa existente");
    }


}


