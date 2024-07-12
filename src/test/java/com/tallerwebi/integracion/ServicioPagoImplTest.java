package com.tallerwebi.integracion;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.tallerwebi.dominio.ServicioPagoImpl;
import com.tallerwebi.dominio.reserva.RepositorioReserva;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ServicioPagoImplTest {

    @Mock
    private RepositorioReserva repositorioReserva;

    @InjectMocks
    private ServicioPagoImpl servicioPago;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void dadoQueDatosValidosCuandoCrearPreferenciaEntoncesDevolverIdPreferencia() throws MPException, MPApiException {
        String preferenceId = servicioPago.createPreference("title", 1, 100.0, 10.0, 1);
        assertNotNull(preferenceId);
    }

    @Test
    public void dadoQueCollectionStatusAprobadoCuandoVerificarPagoEntoncesDevolverTrue() {
        boolean result = servicioPago.verificarPagoConServidor("approved");
        assertTrue(result);
    }

    @Test
    public void dadoQueIdReservaYEstadoCuandoRegistrarReservaEntoncesLlamarActualizarEstadoReserva() {
        Long idReserva = 1L;
        String estado = "APROBADO";
        servicioPago.registrarLaReservaComoAprobada(idReserva, estado);
        verify(repositorioReserva).actualizarEstadoReserva(idReserva, estado);
    }
}
