package com.tallerwebi.integracion;

import com.tallerwebi.dominio.Locker;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.reserva.ServicioReserva;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import com.tallerwebi.presentacion.ControladorReserva;
import com.tallerwebi.presentacion.ReservaDatos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {SpringWebTestConfig.class, HibernateTestConfig.class})
public class ControladorReservaTest {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Mock
    private ServicioReserva servicioReservaMock;

    @InjectMocks
    private ControladorReserva controladorReserva;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controladorReserva).build();
    }

    @Test
    public void debeRetornarElFormularioDeReserva() throws Exception {
        MvcResult result = this.mockMvc.perform(get("/reserva"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView modelAndView = result.getModelAndView();
        assert modelAndView != null;
        assertThat(modelAndView.getViewName(), equalToIgnoringCase("formularioReserva"));
        assertThat(modelAndView.getModel().get("reservaDatos").toString(), containsString("com.tallerwebi.presentacion.ReservaDatos"));
    }

    @Test
    public void debeRegistrarReservaExitosamente() throws Exception {
        Usuario usuarioMock = mock(Usuario.class);
        when(servicioReservaMock.consultarUsuarioDeReserva(anyString(), anyString())).thenReturn(usuarioMock);

        MvcResult result = this.mockMvc.perform(post("/reserva")
                        .param("email", "test@example.com")
                        .param("password", "password")
                        .param("idLocker", "1"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView modelAndView = result.getModelAndView();
        assert modelAndView != null;
        assertThat(modelAndView.getViewName(), equalToIgnoringCase("resultadoReserva"));
        assertThat(modelAndView.getModel().get("mensaje").toString(), containsString("Reserva registrada exitosamente"));
    }

    @Test
    public void debeMostrarMensajeUsuarioNoEncontrado() throws Exception {
        when(servicioReservaMock.consultarUsuarioDeReserva(anyString(), anyString())).thenReturn(null);

        MvcResult result = this.mockMvc.perform(post("/reserva")
                        .param("email", "test@example.com")
                        .param("password", "password")
                        .param("idLocker", "1"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView modelAndView = result.getModelAndView();
        assert modelAndView != null;
        assertThat(modelAndView.getViewName(), equalToIgnoringCase("resultadoReserva"));
        assertThat(modelAndView.getModel().get("mensaje").toString(), containsString("Usuario no encontrado o credenciales incorrectas"));
    }

    @Test
    public void debeMostrarMensajeUsuarioExistente() throws Exception {
        Usuario usuarioMock = mock(Usuario.class);
        when(servicioReservaMock.consultarUsuarioDeReserva(anyString(), anyString())).thenReturn(usuarioMock);
        doThrow(new UsuarioExistente()).when(servicioReservaMock).registrarReserva(any(Usuario.class), any(Locker.class));

        MvcResult result = this.mockMvc.perform(post("/reserva")
                        .param("email", "test@example.com")
                        .param("password", "password")
                        .param("idLocker", "1"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView modelAndView = result.getModelAndView();
        assert modelAndView != null;
        assertThat(modelAndView.getViewName(), equalToIgnoringCase("resultadoReserva"));
        assertThat(modelAndView.getModel().get("mensaje").toString(), containsString("El usuario ya tiene una reserva activa"));
    }

    @Test
    public void debeMostrarMensajeErrorAlRegistrarReserva() throws Exception {
        Usuario usuarioMock = mock(Usuario.class);
        when(servicioReservaMock.consultarUsuarioDeReserva(anyString(), anyString())).thenReturn(usuarioMock);
        doThrow(new RuntimeException()).when(servicioReservaMock).registrarReserva(any(Usuario.class), any(Locker.class));

        MvcResult result = this.mockMvc.perform(post("/reserva")
                        .param("email", "test@example.com")
                        .param("password", "password")
                        .param("idLocker", "1"))
                .andExpect(status().isOk())
                .andReturn();

        ModelAndView modelAndView = result.getModelAndView();
        assert modelAndView != null;
        assertThat(modelAndView.getViewName(), equalToIgnoringCase("resultadoReserva"));
        assertThat(modelAndView.getModel().get("mensaje").toString(), containsString("Ocurrió un error al registrar la reserva"));
    }
}
