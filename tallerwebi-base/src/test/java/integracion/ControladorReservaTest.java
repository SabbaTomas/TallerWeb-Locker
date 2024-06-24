package integracion;

import dominio.Locker;
import dominio.Usuario;
import dominio.locker.ServicioLocker;
import dominio.reserva.ServicioReserva;
import integracion.config.HibernateTestConfig;
import integracion.config.SpringWebTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.ui.Model;
import presentacion.ControladorReserva;
import dominio.reserva.Reserva;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {HibernateTestConfig.class, SpringWebTestConfig.class})
public class ControladorReservaTest {

    @Mock
    private ServicioReserva servicioReservaMock;

    @Mock
    private ServicioLocker servicioLockerMock;

    @Mock
    private Model modelMock;

    @Mock
    private Model model;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ControladorReserva controladorReserva;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testMostrarFormularioReserva_Exito() {
        Reserva reserva = new Reserva();
        reserva.setUsuario(new Usuario());
        reserva.setLocker(new Locker());

        when(servicioReservaMock.prepararDatosReserva(anyLong(), anyLong())).thenReturn(reserva);

        String vista = controladorReserva.mostrarFormularioReserva(1L, 1L, modelMock);

        assertEquals("formularioReserva", vista);
    }

    @Test
    public void testMostrarFormularioReserva_Error() {
        when(servicioReservaMock.prepararDatosReserva(anyLong(), anyLong())).thenThrow(new IllegalArgumentException("Error de argumento"));

        String vista = controladorReserva.mostrarFormularioReserva(1L, 1L, modelMock);

        assertEquals("error", vista);
    }

    @Test
    public void testRegistrarReserva_Exito() throws Exception {
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = LocalDate.now().plusDays(1);
        Locker locker = new Locker();
        Usuario usuario = new Usuario();

        when(servicioLockerMock.obtenerLockerPorId(anyLong())).thenReturn(locker);
        when(servicioReservaMock.tieneReservaActiva(anyLong(),anyLong())).thenReturn(false);
        when(servicioReservaMock.calcularCosto(any(LocalDate.class), any(LocalDate.class))).thenReturn(100.0);
        when(servicioReservaMock.registrarReserva(any(Reserva.class))).thenReturn(new Reserva());

        String vista = controladorReserva.registrarReserva(
                fechaInicio.toString(),
                fechaFin.toString(),
                1L,
                1L,
                "test@example.com",
                "password",
                modelMock
        );

        assertEquals("resultadoReserva", vista);
    }

    @Test
    public void testRegistrarReserva_UsuarioExistente() throws Exception {
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = LocalDate.now().plusDays(1);
        Locker locker = new Locker();

        when(servicioLockerMock.obtenerLockerPorId(anyLong())).thenReturn(locker);
        when(servicioReservaMock.tieneReservaActiva(anyLong(),anyLong())).thenReturn(true);

        String vista = controladorReserva.registrarReserva(
                fechaInicio.toString(),
                fechaFin.toString(),
                1L,
                1L,
                "test@example.com",
                "password",
                modelMock
        );

        assertEquals("resultadoReserva", vista);
    }

    @Test
    public void testRegistrarReserva_ErrorAlRegistrar() throws Exception {
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = LocalDate.now().plusDays(1);
        Locker locker = new Locker();

        when(servicioLockerMock.obtenerLockerPorId(anyLong())).thenReturn(locker);
        when(servicioReservaMock.tieneReservaActiva(anyLong(),anyLong())).thenReturn(false);
        doThrow(new RuntimeException()).when(servicioReservaMock).registrarReserva(any(Reserva.class));

        String vista = controladorReserva.registrarReserva(
                fechaInicio.toString(),
                fechaFin.toString(),
                1L,
                1L,
                "test@example.com",
                "password",
                modelMock
        );

        assertEquals("resultadoReserva", vista);
    }

    //Integracion

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

        when(servicioReservaMock.prepararDatosReserva(idUsuario, idLocker)).thenReturn(reserva);

        String viewName = controladorReserva.mostrarFormularioReserva(idUsuario, idLocker, model);

        assertEquals("formularioReserva", viewName);
        verify(model, times(1)).addAttribute("reserva", reserva);
        verify(model, times(1)).addAttribute("usuario", usuario);
        verify(model, times(1)).addAttribute("locker", locker);
    }


    @Test
    public void testRegistrarReserva() throws Exception {
        Long idUsuario = 1L;
        Long idLocker = 1L;
        String email = "test@example.com";
        String password = "password";
        String fechaReserva = "2024-06-01";
        String fechaFinalizacion = "2024-06-10";

        Usuario usuario = new Usuario();
        usuario.setId(idUsuario);
        usuario.setEmail(email);
        usuario.setPassword(password);

        Locker locker = new Locker();
        locker.setId(idLocker);

        when(servicioLockerMock.obtenerLockerPorId(idLocker)).thenReturn(locker);
        when(servicioReservaMock.tieneReservaActiva(idUsuario,idLocker)).thenReturn(false);
        when(servicioReservaMock.calcularCosto(LocalDate.parse(fechaReserva), LocalDate.parse(fechaFinalizacion))).thenReturn(100.0);

        Reserva reserva = new Reserva();
        reserva.setFechaReserva(LocalDate.parse(fechaReserva));
        reserva.setFechaFinalizacion(LocalDate.parse(fechaFinalizacion));
        reserva.setLocker(locker);
        reserva.setUsuario(usuario);
        reserva.setCosto(100.0);

        when(servicioReservaMock.registrarReserva(any(Reserva.class))).thenReturn(reserva);
        when(objectMapper.writeValueAsString(any(Reserva.class))).thenReturn("{\"json\":\"value\"}");

        String viewName = controladorReserva.registrarReserva(fechaReserva, fechaFinalizacion, idLocker, idUsuario, email, password, model);

        assertEquals("resultadoReserva", viewName);
        verify(model).addAttribute("mensaje", "Reserva registrada exitosamente");
        verify(model).addAttribute("lockerId", idLocker);
        verify(model).addAttribute("costoTotal", 100.0);
        verify(model).addAttribute("usuarioId", idUsuario);
        verify(model).addAttribute("cantidadLockers", 1);
        verify(model).addAttribute("fechaInicio", LocalDate.parse(fechaReserva));
        verify(model).addAttribute("fechaFin", LocalDate.parse(fechaFinalizacion));
        verify(model).addAttribute("jsonReserva", "{\"json\":\"value\"}");
    }
}
