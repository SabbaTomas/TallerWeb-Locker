package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Locker;
import com.tallerwebi.dominio.locker.ServicioLocker;
import com.tallerwebi.dominio.locker.TipoLocker;
import com.tallerwebi.integracion.config.HibernateTestConfig;
import com.tallerwebi.integracion.config.SpringWebTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {HibernateTestConfig.class, SpringWebTestConfig.class})
public class ControladorLockerTest {

    private ControladorLocker controladorLocker;

    @Mock
    private ServicioLocker servicioLocker;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        controladorLocker = new ControladorLocker(servicioLocker);
    }

    @Test
    public void queSePuedaCrearUnNuevoLocker() {
        // Preparación
        TipoLocker tipoLocker = TipoLocker.PEQUENIO;
        List<Locker> lockers = new ArrayList<>();
        when(servicioLocker.obtenerLockersPorTipo(tipoLocker)).thenReturn(lockers);

        // Ejecución
        ModelAndView mav = controladorLocker.crearLocker(tipoLocker);

        // Verificación
        assertThat(mav.getViewName(), equalToIgnoringCase("crear-locker"));
        assertThat(mav.getModel().get("lockers"), equalTo(lockers));
    }

    @Test
    public void queSePuedaActualizarUnLocker() {
        // Preparación
        Long idLocker = 1L;
        TipoLocker tipoLocker = TipoLocker.GRANDE;

        // Ejecución
        ModelAndView mav = controladorLocker.actualizarLocker(idLocker, tipoLocker);

        // Verificación
        assertThat(mav.getViewName(), equalToIgnoringCase("envio-actualizar"));
        verify(servicioLocker).actualizarLocker(idLocker, tipoLocker);
    }

    @Test
    public void queSePuedaEliminarUnLocker() {
        // Preparación
        Long idLocker = 1L;

        // Ejecución
        ModelAndView mav = controladorLocker.eliminarLocker(idLocker);

        // Verificación
        assertThat(mav.getViewName(), equalToIgnoringCase("envio-eliminar"));
        verify(servicioLocker).eliminarLocker(idLocker);
    }

    @Test
    public void queSePuedaObtenerUnaListaDeLockersFiltradaPorTipo() {
        // Preparación
        TipoLocker tipoLocker = TipoLocker.GRANDE;
        List<Locker> lockers = new ArrayList<>();
        when(servicioLocker.obtenerLockersPorTipo(tipoLocker)).thenReturn(lockers);

        // Ejecución
        ModelAndView mav = controladorLocker.buscarLockersPorTipo(tipoLocker);

        // Verificación
        assertThat(mav.getViewName(), equalToIgnoringCase("lockers-por-tipo"));
        assertThat(mav.getModel().get("lockers"), equalTo(lockers));
    }

    @Test
    public void queSeMuestrenLockersSeleccionados() {
        // Preparación
        List<Locker> lockers = Arrays.asList(
                new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "1704"),
                new Locker(TipoLocker.MEDIANO, 40.7129, -74.0061, "1704")
        );
        when(servicioLocker.obtenerLockersSeleccionados()).thenReturn(lockers);

        // Ejecución
        ModelAndView mav = controladorLocker.mostrarLockers();

        // Verificación
        assertThat(mav.getViewName(), equalToIgnoringCase("lockers"));
        assertThat(mav.getModel().get("lockers"), equalTo(lockers));
    }

    @Test
    public void buscarLockersPorCodigoPostal_CuandoSeProporcionaCodigoPostal() {
        // Preparación
        String codigoPostal = "1704";
        List<Locker> lockers = Arrays.asList(
                new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, codigoPostal),
                new Locker(TipoLocker.MEDIANO, 40.7129, -74.0061, codigoPostal)
        );
        when(servicioLocker.buscarLockers(eq(codigoPostal), isNull(), isNull(), anyDouble())).thenReturn(lockers);

        // Ejecución
        ModelAndView mav = controladorLocker.buscarLockersPorCodigoPostal(codigoPostal, null, null);

        // Verificación
        assertThat(mav.getViewName(), equalToIgnoringCase("lockers"));
        assertThat(mav.getModel().get("lockers"), equalTo(lockers));
        assertThat(mav.getModel().get("codigoPostal"), equalTo(codigoPostal));
        assertNull(mav.getModel().get("latitud"));
        assertNull(mav.getModel().get("longitud"));
        assertFalse((Boolean) mav.getModel().get("mostrarAlternativos"));
    }

    @Test
    public void queNoSeMuestrenLockersSiNoHayDatos() {
        // Mockear el servicio para devolver una lista vacía
        when(servicioLocker.obtenerLockersSeleccionados()).thenReturn(new ArrayList<>());

        // Ejecución
        ModelAndView mav = controladorLocker.mostrarLockers();

        // Verificación
        assertThat(mav.getViewName(), equalToIgnoringCase("lockers"));
        assertThat(mav.getModel().get("lockers"), instanceOf(List.class));
        assertTrue(((List<?>) mav.getModel().get("lockers")).isEmpty());
    }

    @Test
    public void buscarLockersPorLatitudYLongitud_DevuelveLockersCorrectos() {
        // Preparación
        Double latitud = -34.6821;
        Double longitud = -58.5638;
        List<Locker> lockers = Arrays.asList(
                new Locker(TipoLocker.PEQUENIO, latitud, longitud, "1704"),
                new Locker(TipoLocker.MEDIANO, latitud + 0.001, longitud - 0.001, "1704")
        );
        when(servicioLocker.buscarLockers(isNull(), eq(latitud), eq(longitud), anyDouble())).thenReturn(lockers);

        // Ejecución
        ModelAndView mav = controladorLocker.buscarLockersPorCodigoPostal(null, latitud, longitud);

        // Verificación
        assertThat(mav.getViewName(), equalToIgnoringCase("lockers"));
        assertThat(mav.getModel().get("lockers"), equalTo(lockers));
    }

    @Test
    public void buscarLockers_SinParametros_DevuelveLockersSeleccionados() {
        // Preparación
        List<Locker> lockersSeleccionados = Arrays.asList(
                new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "1704"),
                new Locker(TipoLocker.MEDIANO, 40.7129, -74.0061, "1704")
        );
        when(servicioLocker.buscarLockers(isNull(), isNull(), isNull(), anyDouble())).thenReturn(Collections.emptyList());
        when(servicioLocker.obtenerLockersSeleccionados()).thenReturn(lockersSeleccionados);

        // Ejecución
        ModelAndView mav = controladorLocker.buscarLockersPorCodigoPostal(null, null, null);

        // Verificación
        assertThat(mav.getViewName(), equalToIgnoringCase("lockers"));
        assertThat(mav.getModel().get("lockers"), equalTo(lockersSeleccionados));
    }

    @Test
    public void buscarLockers_SinResultados_DevuelveLockersAlternativos() {
        // Preparación
        String codigoPostal = "1234";
        List<Locker> lockersSeleccionados = Arrays.asList(
                new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, codigoPostal),
                new Locker(TipoLocker.MEDIANO, 40.7129, -74.0061, codigoPostal)
        );
        when(servicioLocker.buscarLockers(eq(codigoPostal), isNull(), isNull(), anyDouble())).thenReturn(Collections.emptyList());
        when(servicioLocker.obtenerLockersSeleccionados()).thenReturn(lockersSeleccionados);

        // Ejecución
        ModelAndView mav = controladorLocker.buscarLockersPorCodigoPostal(codigoPostal, null, null);

        // Verificación
        assertThat(mav.getViewName(), equalToIgnoringCase("lockers"));
        assertThat(mav.getModel().get("lockers"), equalTo(lockersSeleccionados));
        assertTrue((Boolean) mav.getModel().get("mostrarAlternativos"));
    }

    @Test
    public void buscarLockersPorCodigoPostal_ConLatitudYLongitud_DevuelveLockersCorrectos() {
        // Preparación
        String codigoPostal = "1704";
        Double latitud = -34.6821;
        Double longitud = -58.5638;
        List<Locker> lockers = Arrays.asList(
                new Locker(TipoLocker.PEQUENIO, latitud, longitud, codigoPostal),
                new Locker(TipoLocker.MEDIANO, latitud + 0.001, longitud - 0.001, codigoPostal)
        );
        when(servicioLocker.buscarLockers(eq(codigoPostal), eq(latitud), eq(longitud), anyDouble())).thenReturn(lockers);

        // Ejecución
        ModelAndView mav = controladorLocker.buscarLockersPorCodigoPostal(codigoPostal, latitud, longitud);

        // Verificación
        assertThat(mav.getViewName(), equalToIgnoringCase("lockers"));
        assertThat(mav.getModel().get("lockers"), equalTo(lockers));
    }
}
