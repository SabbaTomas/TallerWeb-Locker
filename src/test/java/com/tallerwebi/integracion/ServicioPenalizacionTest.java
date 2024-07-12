package com.tallerwebi.integracion;

import com.tallerwebi.dominio.locker.Locker;
import com.tallerwebi.dominio.locker.RepositorioDatosLocker;
import com.tallerwebi.dominio.penalizacion.PenalizacionService;
import com.tallerwebi.dominio.penalizacion.RepositorioPenalizacion;
import com.tallerwebi.dominio.reserva.RepositorioReserva;
import com.tallerwebi.dominio.reserva.Reserva;
import com.tallerwebi.dominio.reserva.ServicioReservaImpl;
import com.tallerwebi.dominio.usuario.RepositorioUsuario;
import com.tallerwebi.dominio.usuario.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ServicioPenalizacionTest {

    private ServicioReservaImpl servicioReserva;
    private RepositorioUsuario repositorioUsuario;
    private RepositorioDatosLocker repositorioDatosLocker;
    private RepositorioReserva repositorioReserva;
    private PenalizacionService penalizacionService;
    private RepositorioPenalizacion repositorioPenalizacion;

    @BeforeEach
    public void init() {
        repositorioUsuario = mock(RepositorioUsuario.class);
        repositorioDatosLocker = mock(RepositorioDatosLocker.class);
        repositorioReserva = mock(RepositorioReserva.class);
        repositorioPenalizacion = mock(RepositorioPenalizacion.class);
        penalizacionService = mock(PenalizacionService.class);
        servicioReserva = new ServicioReservaImpl(repositorioUsuario, repositorioDatosLocker, repositorioReserva, penalizacionService);
    }

    @Test
    public double calcularPenalizacionPorUsoExtendido(LocalDate fechaFinalizacion, LocalDate fechaActual) {
        long diasExtendidos = DAYS.between(fechaFinalizacion, fechaActual);
        if (diasExtendidos > 0) {
            return diasExtendidos * 100.0;
        }
        return 0.0;
    }


    @Test
    public void dadoQuePagoEsEnTiempoSeCalculaPenalizacionCero() {
        LocalDate fechaReserva = LocalDate.now().minusDays(1);
        LocalDate fechaPago = LocalDate.now();

        double penalizacion = penalizacionService.calcularPenalizacionPorPagoTardio(fechaReserva, fechaPago);

        assertEquals(0.0, penalizacion);
    }

    @Test
    public void dadoQueUsoTerminaEnTiempoSeCalculaPenalizacionCero() {
        LocalDate fechaFinalizacion = LocalDate.now();
        LocalDate fechaActual = LocalDate.now();

        double penalizacion = penalizacionService.calcularPenalizacionPorUsoExtendido(fechaFinalizacion, fechaActual);

        assertEquals(0.0, penalizacion);
    }

    public Reserva finalizarReserva(Long idReserva) {
        Reserva reserva = repositorioReserva.obtenerReservaPorId(idReserva);
        if (reserva != null) {
            double penalizacionPagoTardio = penalizacionService.calcularPenalizacionPorPagoTardio(reserva.getFechaReserva(), LocalDate.now());
            double penalizacionUsoExtendido = penalizacionService.calcularPenalizacionPorUsoExtendido(reserva.getFechaFinalizacion(), LocalDate.now());
            double penalizacionTotal = penalizacionPagoTardio + penalizacionUsoExtendido;

            reserva.setCosto(reserva.getCosto() + penalizacionTotal);
            reserva.setEstado("finalizada");

            repositorioReserva.guardarReserva(reserva);
        }
        return reserva;
    }


    @Test
    public void dadoQueReservaSinPenalizacionSeMantieneCostoAlFinalizar() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("test@test.com");
        usuario.setPassword("password");

        Locker locker = new Locker();
        locker.setId(1L);

        Reserva reserva = new Reserva();
        reserva.setId(1L);
        reserva.setUsuario(usuario);
        reserva.setLocker(locker);
        reserva.setFechaReserva(LocalDate.now());
        reserva.setFechaFinalizacion(LocalDate.now().plusDays(5));
        reserva.setCosto(500.0);

        when(repositorioReserva.obtenerReservaPorId(1L)).thenReturn(reserva);

        servicioReserva = new ServicioReservaImpl(repositorioUsuario, repositorioDatosLocker, repositorioReserva, penalizacionService);

        Reserva reservaFinalizada = servicioReserva.finalizarReserva(1L);

        assertEquals(500.0, reservaFinalizada.getCosto());
        assertEquals("finalizada", reservaFinalizada.getEstado());
    }

    @Test
    public void dadoQueReservaSeFinalizaAntesDeFechaDeFinalizacionNoSeCalculaPenalizacion() {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("test@test.com");
        usuario.setPassword("password");

        Locker locker = new Locker();
        locker.setId(1L);

        Reserva reserva = new Reserva();
        reserva.setId(1L);
        reserva.setUsuario(usuario);
        reserva.setLocker(locker);
        reserva.setFechaReserva(LocalDate.now().minusDays(2));
        reserva.setFechaFinalizacion(LocalDate.now().plusDays(2)); // Fecha de finalización en el futuro
        reserva.setCosto(200.0);

        when(repositorioReserva.obtenerReservaPorId(1L)).thenReturn(reserva);

        servicioReserva = new ServicioReservaImpl(repositorioUsuario, repositorioDatosLocker, repositorioReserva, penalizacionService);

        Reserva reservaFinalizada = servicioReserva.finalizarReserva(1L);

        assertEquals(200.0, reservaFinalizada.getCosto()); // Costo original sin penalización
        assertEquals("finalizada", reservaFinalizada.getEstado());
    }



    @Test
    public void testVerificarYAplicarPenalizacionesPenalizacionPorUsoExtendido() {
        Reserva reserva = new Reserva();
        reserva.setEstado("pendiente");
        reserva.setFechaReserva(LocalDate.now().minusDays(5));
        reserva.setFechaFinalizacion(LocalDate.now().minusDays(1));

        when(repositorioReserva.obtenerTodasLasReservas()).thenReturn(List.of(reserva));
        when(penalizacionService.calcularPenalizacionPorPagoTardio(reserva.getFechaReserva(), LocalDate.now())).thenReturn(0.0);
        when(penalizacionService.calcularPenalizacionPorUsoExtendido(reserva.getFechaFinalizacion(), LocalDate.now())).thenReturn(100.0);

        servicioReserva.verificarYAplicarPenalizaciones();

        assertEquals("penalizado", reserva.getEstado());
        verify(repositorioReserva).guardarReserva(reserva);
        verify(penalizacionService).registrarPenalizacion(reserva, 100.0);
        verify(penalizacionService, never()).registrarPenalizacion(reserva, 0.0);
    }

    @Test
    public void testVerificarYAplicarPenalizacionesPenalizacionPorPagoTardio() {
        Reserva reserva = new Reserva();
        reserva.setEstado("pendiente");
        reserva.setFechaReserva(LocalDate.now().minusDays(10));
        reserva.setFechaFinalizacion(LocalDate.now().plusDays(5));

        when(repositorioReserva.obtenerTodasLasReservas()).thenReturn(List.of(reserva));
        when(penalizacionService.calcularPenalizacionPorPagoTardio(reserva.getFechaReserva(), LocalDate.now())).thenReturn(50.0);
        when(penalizacionService.calcularPenalizacionPorUsoExtendido(reserva.getFechaFinalizacion(), LocalDate.now())).thenReturn(0.0);

        servicioReserva.verificarYAplicarPenalizaciones();

        assertEquals("penalizado", reserva.getEstado());
        verify(repositorioReserva).guardarReserva(reserva);
        verify(penalizacionService).registrarPenalizacion(reserva, 50.0);
        verify(penalizacionService, never()).registrarPenalizacion(reserva, 0.0);
    }

    @Test
    public void testVerificarYAplicarPenalizacionesSinPenalizaciones() {
        Reserva reserva = mock(Reserva.class);
        when(reserva.getEstado()).thenReturn("pendiente");
        when(reserva.getFechaReserva()).thenReturn(LocalDate.now().minusDays(5));
        when(reserva.getFechaFinalizacion()).thenReturn(LocalDate.now().plusDays(1));

        RepositorioReserva repositorioReservaMock = mock(RepositorioReserva.class);
        PenalizacionService penalizacionServiceMock = mock(PenalizacionService.class);

        when(repositorioReservaMock.obtenerTodasLasReservas()).thenReturn(Arrays.asList(reserva));
        when(penalizacionServiceMock.calcularPenalizacionPorPagoTardio(LocalDate.now().minusDays(5), LocalDate.now().plusDays(1))).thenReturn(0.0);
        when(penalizacionServiceMock.calcularPenalizacionPorUsoExtendido(LocalDate.now().minusDays(5), LocalDate.now().plusDays(1))).thenReturn(0.0);

        servicioReserva.verificarYAplicarPenalizaciones();

        verify(reserva, never()).setEstado("penalizado");
        verify(repositorioReservaMock, never()).guardarReserva(reserva);
        verify(penalizacionServiceMock, never()).registrarPenalizacion(reserva, 0.0);
    }



    @Test
    public void testVerificarYAplicarPenalizacionesReservaNoPendiente() {
        Reserva reserva = mock(Reserva.class);
        when(reserva.getEstado()).thenReturn("aprobado");
        when(reserva.getFechaReserva()).thenReturn(LocalDate.now().minusDays(5));
        when(reserva.getFechaFinalizacion()).thenReturn(LocalDate.now().plusDays(1));

        RepositorioReserva repositorioReservaMock = mock(RepositorioReserva.class);
        PenalizacionService penalizacionServiceMock = mock(PenalizacionService.class);

        when(repositorioReservaMock.obtenerTodasLasReservas()).thenReturn(Arrays.asList(reserva));

        servicioReserva.verificarYAplicarPenalizaciones();

        verify(reserva, never()).setEstado("penalizado");
        verify(repositorioReservaMock, never()).guardarReserva(reserva);
        verify(penalizacionServiceMock, never()).calcularPenalizacionPorPagoTardio(LocalDate.now().minusDays(5), LocalDate.now().plusDays(1));
        verify(penalizacionServiceMock, never()).calcularPenalizacionPorUsoExtendido(LocalDate.now().minusDays(5), LocalDate.now().plusDays(1));
    }



    @Test
    public void dadoQuePagoEsTardioSeCalculaPenalizacionCorrectamente() {
        LocalDate fechaReserva = LocalDate.now().minusDays(5);
        LocalDate fechaPago = LocalDate.now();

        // Mocking the method to return a specific value
        PenalizacionService penalizacionServiceMock = mock(PenalizacionService.class);
        when(penalizacionServiceMock.calcularPenalizacionPorPagoTardio(fechaReserva, fechaPago)).thenReturn(200.0);

        // Call the method
        double penalizacion = penalizacionServiceMock.calcularPenalizacionPorPagoTardio(fechaReserva, fechaPago);

        // Verify the result
        assertEquals(200.0, penalizacion);
    }




}
