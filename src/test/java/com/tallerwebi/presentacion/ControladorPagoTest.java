package com.tallerwebi.presentacion;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tallerwebi.dominio.ServicioPago;
import com.tallerwebi.dominio.penalizacion.PenalizacionService;
import com.tallerwebi.presentacion.ControladorPago;
import com.tallerwebi.presentacion.UserBuyer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

public class ControladorPagoTest {

    private MockMvc mockMvc;

    @Mock
    private ServicioPago paymentService;

    @Mock
    private PenalizacionService penalizacionService;

    @InjectMocks
    private ControladorPago controladorPago;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controladorPago).build();
    }

  /*
    @Test
    public void dadoQueReservaSeleccionadaYMontoTotalCuandoLlamarAPagarPenalizacionesEntoncesDevolverModeloConDatos() throws Exception {
        mockMvc.perform(get("/pagarPenalizaciones")
                        .param("reservaSeleccionada", "1")
                        .param("montoTotal", "100.0"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("reservaId"))
                .andExpect(model().attributeExists("cantidadPenalizaciones"))
                .andExpect(model().attributeExists("montoTotal"))
                .andExpect(model().attributeExists("penalizaciones"))
                .andExpect(view().name("pagarPenalizaciones"));
    }

    @Test
    public void dadoQueReservaIdYCostoTotalCuandollamarAPagarEntoncesDevolverModeloConDatos() throws Exception {
        mockMvc.perform(get("/pagar")
                        .param("reservaId", "1")
                        .param("costoTotal", "100.0"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("reservaId"))
                .andExpect(model().attributeExists("mensaje"))
                .andExpect(view().name("pagar"));
    }


   */
    @Test
    public void dadoQueUserBuyerNuloCuandoLlamarAPI_MPEntoncesDevolverError() throws Exception {
        String response = controladorPago.getlist(null);
        assertEquals("error jsons", response);
    }

    @Test
    public void dadoQueUserBuyerValidoCuandoLlamarAPI_MPEntoncesDevolverPreferencia() throws Exception {
        UserBuyer userBuyer = new UserBuyer();
        userBuyer.setTitle("title");
        userBuyer.setQuantity(1);
        userBuyer.setUnit_price(100.0);
        userBuyer.setPenalizacion(10.0);
        userBuyer.setId(1);

        when(paymentService.createPreference(anyString(), anyInt(), anyDouble(), anyDouble(), anyInt()))
                .thenReturn("preferenceId");

        String response = controladorPago.getlist(userBuyer);
        assertEquals("preferenceId", response);
    }
}
