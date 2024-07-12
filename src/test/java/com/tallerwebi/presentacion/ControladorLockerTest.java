package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.locker.Locker;
import com.tallerwebi.dominio.locker.ServicioLocker;
import com.tallerwebi.dominio.locker.Enum.TipoLocker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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

@WebAppConfiguration
public class ControladorLockerTest {

    @InjectMocks
    private ControladorLocker controladorLocker;

    @Mock
    private ServicioLocker servicioLocker;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        controladorLocker = new ControladorLocker(servicioLocker);
    }

    // Tests para mostrarFormularioCrearLocker
    @Test
    public void dadoQueSeMuestraFormularioCrearLockerEntoncesDevuelveVistaCorrecta() {
        TipoLocker tipoLocker = TipoLocker.PEQUENIO;
        ModelAndView mav = controladorLocker.mostrarFormularioCrearLocker(tipoLocker);
        assertThat(mav.getViewName(), equalToIgnoringCase("crear-locker"));
        assertNotNull(mav.getModel().get("nuevoLocker"));
    }

    // Tests para crearLocker
    @Test
    public void dadoQueSeCreaUnNuevoLockerEntoncesDevuelveVistaCorrecta() {
        Locker nuevoLocker = new Locker(TipoLocker.PEQUENIO, -34.605, -58.510, "1704");
        doNothing().when(servicioLocker).crearLocker(nuevoLocker);

        ModelAndView mav = controladorLocker.crearLocker(nuevoLocker);

        assertThat(mav.getViewName(), equalToIgnoringCase("mensaje-creacion"));
        assertThat(mav.getModel().get("message"), equalTo("¡Locker creado exitosamente!"));
        assertThat(mav.getModel().get("nuevoLocker"), equalTo(nuevoLocker));
    }

    @Test
    public void dadoQueSeIntentaCrearUnNuevoLockerEntoncesManejaExcepcion() {
        Locker nuevoLocker = new Locker(TipoLocker.PEQUENIO, -34.605, -58.510, "1704");
        doThrow(new RuntimeException("Error al crear locker")).when(servicioLocker).crearLocker(nuevoLocker);

        ModelAndView mav = controladorLocker.crearLocker(nuevoLocker);

        assertThat(mav.getViewName(), equalToIgnoringCase("crear-locker"));
        assertThat(mav.getModel().get("errorMessage"), equalTo("Error al crear locker."));
        assertThat(mav.getModel().get("nuevoLocker"), equalTo(nuevoLocker));
    }

    @Test
    public void dadoQueSePuedaCrearUnNuevoLockerYNoDeNulo() {
        TipoLocker tipoLocker = TipoLocker.PEQUENIO;
        Locker locker = new Locker(tipoLocker, -34.605, -58.510, "1704");
        ModelAndView nuevoLocker = controladorLocker.crearLocker(locker);

        assertNotNull(nuevoLocker, "El nuevo Locker debería ser creado y no nulo");
    }

    @Test
    public void dadoQueSeCreaUnLockerConValoresEspecificosEntoncesLosValoresSonCorrectos() {
        TipoLocker tipoLocker = TipoLocker.MEDIANO;
        Locker locker = new Locker(tipoLocker, -34.605, -58.510, "1704");
        doNothing().when(servicioLocker).crearLocker(locker);

        servicioLocker.crearLocker(locker);

        assertNotNull(locker);
        assertThat(locker.getTipo(), equalTo(tipoLocker));
        assertThat(locker.getLatitud(), equalTo(-34.605));
        assertThat(locker.getLongitud(), equalTo(-58.510));
        assertThat(locker.getCodigo_postal(), equalTo("1704"));
    }

    // Tests para mostrarFormularioActualizar
    @Test
    public void dadoQueSeMuestraFormularioActualizarEntoncesDevuelveVistaCorrecta() {
        String viewName = controladorLocker.mostrarFormularioActualizar(null);
        assertThat(viewName, equalToIgnoringCase("envio-actualizar-form"));
    }

    // Tests para actualizarLocker
    @Test
    public void dadoQueSeActualizaLockerEntoncesDevuelveVistaCorrecta() {
        Long idLocker = 1L;
        TipoLocker tipoLocker = TipoLocker.GRANDE;
        doNothing().when(servicioLocker).actualizarLocker(idLocker, tipoLocker);

        ModelAndView mav = controladorLocker.actualizarLocker(idLocker, tipoLocker);

        assertThat(mav.getViewName(), equalToIgnoringCase("envio-actualizar"));
        verify(servicioLocker).actualizarLocker(idLocker, tipoLocker);
    }

    @Test
    public void dadoQueSeIntentaActualizarLockerEntoncesManejaExcepcion() {
        Long idLocker = 1L;
        TipoLocker tipoLocker = TipoLocker.GRANDE;
        doThrow(new RuntimeException("Error al actualizar locker")).when(servicioLocker).actualizarLocker(idLocker, tipoLocker);

        ModelAndView mav = controladorLocker.actualizarLocker(idLocker, tipoLocker);

        assertThat(mav.getViewName(), equalToIgnoringCase("error"));
        assertThat(mav.getModel().get("errorMessage"), equalTo("Error al actualizar locker: Error al actualizar locker"));
    }

    @Test
    public void dadoQueSePuedaActualizarUnLockerEntoncesDevuelveVistaCorrecta() {
        Long idLocker = 1L;
        TipoLocker tipoLocker = TipoLocker.GRANDE;

        ModelAndView mav = controladorLocker.actualizarLocker(idLocker, tipoLocker);

        assertThat(mav.getViewName(), equalToIgnoringCase("envio-actualizar"));
        verify(servicioLocker).actualizarLocker(idLocker, tipoLocker);
    }

    // Tests para mostrarFormularioEliminar
    @Test
    public void dadoQueSeMuestraFormularioEliminarEntoncesDevuelveVistaCorrecta() {
        String viewName = controladorLocker.mostrarFormularioEliminar(null);
        assertThat(viewName, equalToIgnoringCase("envio-eliminar-form"));
    }

    // Tests para eliminarLocker
    @Test
    public void dadoQueSeEliminaLockerEntoncesDevuelveVistaCorrecta() {
        Long idLocker = 1L;
        doNothing().when(servicioLocker).eliminarLocker(idLocker);

        ModelAndView mav = controladorLocker.eliminarLocker(idLocker);

        assertThat(mav.getViewName(), equalToIgnoringCase("envio-eliminar"));
        verify(servicioLocker).eliminarLocker(idLocker);
    }

    @Test
    public void dadoQueSeIntentaEliminarLockerEntoncesManejaExcepcion() {
        Long idLocker = 1L;
        doThrow(new RuntimeException("Error al eliminar locker")).when(servicioLocker).eliminarLocker(idLocker);

        ModelAndView mav = controladorLocker.eliminarLocker(idLocker);

        assertThat(mav.getViewName(), equalToIgnoringCase("error"));
        assertThat(mav.getModel().get("errorMessage"), equalTo("Error al eliminar locker."));
    }

    @Test
    public void dadoQueSePuedaEliminarUnLockerEntoncesDevuelveVistaCorrecta() {
        Long idLocker = 1L;

        ModelAndView mav = controladorLocker.eliminarLocker(idLocker);

        assertThat(mav.getViewName(), equalToIgnoringCase("envio-eliminar"));
        verify(servicioLocker).eliminarLocker(idLocker);
    }

    // Tests para buscarLockersPorTipo
    @Test
    public void dadoQueSeBuscanLockersPorTipoEntoncesDevuelveVistaCorrecta() {
        TipoLocker tipoLocker = TipoLocker.GRANDE;
        List<Locker> lockers = new ArrayList<>();
        when(servicioLocker.obtenerLockersPorTipo(tipoLocker)).thenReturn(lockers);

        ModelAndView mav = controladorLocker.buscarLockersPorTipo(tipoLocker);

        assertThat(mav.getViewName(), equalToIgnoringCase("lockers-por-tipo"));
        assertThat(mav.getModel().get("lockers"), equalTo(lockers));
    }

    @Test
    public void dadoQueSeIntentaBuscarLockersPorTipoEntoncesManejaExcepcion() {
        TipoLocker tipoLocker = TipoLocker.GRANDE;
        when(servicioLocker.obtenerLockersPorTipo(tipoLocker)).thenThrow(new RuntimeException("Error al buscar lockers por tipo"));

        ModelAndView mav = controladorLocker.buscarLockersPorTipo(tipoLocker);

        assertThat(mav.getViewName(), equalToIgnoringCase("error"));
        assertThat(mav.getModel().get("errorMessage"), equalTo("Error al buscar lockers por tipo."));
    }

    // Tests para mostrarLockers
    @Test
    public void dadoQueSeMuestranLockersEntoncesDevuelveVistaCorrecta() {
        List<Locker> lockers = Arrays.asList(
                new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "1704"),
                new Locker(TipoLocker.MEDIANO, 40.7129, -74.0061, "1704")
        );
        when(servicioLocker.obtenerLockersSeleccionados()).thenReturn(lockers);

        ModelAndView mav = controladorLocker.mostrarLockers();

        assertThat(mav.getViewName(), equalToIgnoringCase("lockers"));
        assertThat(mav.getModel().get("lockers"), equalTo(lockers));
    }

    @Test
    public void dadoQueSeMuestrenLockersSeleccionadosEntoncesDevuelveVistaCorrecta() {
        List<Locker> lockers = Arrays.asList(
                new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "1704"),
                new Locker(TipoLocker.MEDIANO, 40.7129, -74.0061, "1704")
        );
        when(servicioLocker.obtenerLockersSeleccionados()).thenReturn(lockers);

        ModelAndView mav = controladorLocker.mostrarLockers();

        assertThat(mav.getViewName(), equalToIgnoringCase("lockers"));
        assertThat(mav.getModel().get("lockers"), equalTo(lockers));
    }

    @Test
    public void dadoQueNoSeMuestrenLockersSiNoHayDatosEntoncesDevuelveVistaCorrecta() {
        when(servicioLocker.obtenerLockersSeleccionados()).thenReturn(new ArrayList<>());

        ModelAndView mav = controladorLocker.mostrarLockers();

        assertThat(mav.getViewName(), equalToIgnoringCase("lockers"));
        assertThat(mav.getModel().get("lockers"), instanceOf(List.class));
        assertTrue(((List<?>) mav.getModel().get("lockers")).isEmpty());
    }

    // Tests para buscarLockersPorCodigoPostal
    @Test
    public void dadoQueSeBuscaLockersPorCodigoPostalEntoncesDevuelveVistaCorrecta() {
        String codigoPostal = "1704";
        double latitud = 40.7128;
        double longitud = -74.0060;
        double radio = 5.0;
        List<Locker> lockers = Arrays.asList(
                new Locker(TipoLocker.PEQUENIO, latitud, longitud, codigoPostal),
                new Locker(TipoLocker.MEDIANO, latitud + 0.001, longitud - 0.001, codigoPostal)
        );
        when(servicioLocker.buscarLockers(codigoPostal, latitud, longitud, radio)).thenReturn(lockers);

        ModelAndView mav = controladorLocker.buscarLockersPorCodigoPostal(codigoPostal, latitud, longitud);

        assertThat(mav.getViewName(), equalToIgnoringCase("lockers"));
        assertThat(mav.getModel().get("lockers"), equalTo(lockers));
    }

    @Test
    public void dadoQueSeIntentaBuscarLockersPorCodigoPostalEntoncesManejaExcepcion() {
        String codigoPostal = "1704";
        double latitud = 40.7128;
        double longitud = -74.0060;
        double radio = 5.0;
        when(servicioLocker.buscarLockers(codigoPostal, latitud, longitud, radio)).thenThrow(new RuntimeException("Error al buscar lockers por código postal"));

        ModelAndView mav = controladorLocker.buscarLockersPorCodigoPostal(codigoPostal, latitud, longitud);

        assertThat(mav.getViewName(), equalToIgnoringCase("error"));
        assertThat(mav.getModel().get("errorMessage"), equalTo("Error al buscar lockers."));
    }



    @Test
    public void dadoQueSeBuscaLockersPorCodigoPostalYLatitudLongitudEntoncesDevuelveVistaCorrecta() {
        String codigoPostal = "1704";
        Double latitud = -34.6821;
        Double longitud = -58.5638;
        List<Locker> lockers = Arrays.asList(
                new Locker(TipoLocker.PEQUENIO, latitud, longitud, codigoPostal),
                new Locker(TipoLocker.MEDIANO, latitud + 0.001, longitud - 0.001, codigoPostal)
        );
        when(servicioLocker.buscarLockers(eq(codigoPostal), eq(latitud), eq(longitud), anyDouble())).thenReturn(lockers);

        ModelAndView mav = controladorLocker.buscarLockersPorCodigoPostal(codigoPostal, latitud, longitud);

        assertThat(mav.getViewName(), equalToIgnoringCase("lockers"));
        assertThat(mav.getModel().get("lockers"), equalTo(lockers));
    }

    @Test
    public void dadoQueSeBuscaLockersPorLatitudYLongitudEntoncesDevuelveVistaCorrecta() {
        Double latitud = -34.6821;
        Double longitud = -58.5638;
        List<Locker> lockers = Arrays.asList(
                new Locker(TipoLocker.PEQUENIO, latitud, longitud, "1704"),
                new Locker(TipoLocker.MEDIANO, latitud + 0.001, longitud - 0.001, "1704")
        );
        when(servicioLocker.buscarLockers(isNull(), eq(latitud), eq(longitud), anyDouble())).thenReturn(lockers);

        ModelAndView mav = controladorLocker.buscarLockersPorCodigoPostal(null, latitud, longitud);

        assertThat(mav.getViewName(), equalToIgnoringCase("lockers"));
        assertThat(mav.getModel().get("lockers"), equalTo(lockers));
    }

    @Test
    public void dadoQueSeBuscaLockersSinParametrosEntoncesDevuelveLockersSeleccionados() {
        List<Locker> lockersSeleccionados = Arrays.asList(
                new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "1704"),
                new Locker(TipoLocker.MEDIANO, 40.7129, -74.0061, "1704")
        );
        when(servicioLocker.buscarLockers(isNull(), isNull(), isNull(), anyDouble())).thenReturn(Collections.emptyList());
        when(servicioLocker.obtenerLockersSeleccionados()).thenReturn(lockersSeleccionados);

        ModelAndView mav = controladorLocker.buscarLockersPorCodigoPostal(null, null, null);

        assertThat(mav.getViewName(), equalToIgnoringCase("lockers"));
        assertThat(mav.getModel().get("lockers"), equalTo(lockersSeleccionados));
    }

    @Test
    public void dadoQueSeBuscaLockersSinResultadosEntoncesDevuelveLockersAlternativos() {
        String codigoPostal = "1234";
        List<Locker> lockersSeleccionados = Arrays.asList(
                new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, codigoPostal),
                new Locker(TipoLocker.MEDIANO, 40.7129, -74.0061, codigoPostal)
        );
        when(servicioLocker.buscarLockers(eq(codigoPostal), isNull(), isNull(), anyDouble())).thenReturn(Collections.emptyList());
        when(servicioLocker.obtenerLockersSeleccionados()).thenReturn(lockersSeleccionados);

        ModelAndView mav = controladorLocker.buscarLockersPorCodigoPostal(codigoPostal, null, null);

        assertThat(mav.getViewName(), equalToIgnoringCase("lockers"));
        assertThat(mav.getModel().get("lockers"), equalTo(lockersSeleccionados));
        assertTrue((Boolean) mav.getModel().get("mostrarAlternativos"));
    }

    @Test
    public void dadoQueBuscarLockersPorCodigoPostalYLatitudYLongitudEntoncesDevuelveLockersCorrectos() {
        String codigoPostal = "1704";
        Double latitud = -34.6821;
        Double longitud = -58.5638;
        List<Locker> lockers = Arrays.asList(
                new Locker(TipoLocker.PEQUENIO, latitud, longitud, codigoPostal),
                new Locker(TipoLocker.MEDIANO, latitud + 0.001, longitud - 0.001, codigoPostal)
        );
        when(servicioLocker.buscarLockers(eq(codigoPostal), eq(latitud), eq(longitud), anyDouble())).thenReturn(lockers);

        ModelAndView mav = controladorLocker.buscarLockersPorCodigoPostal(codigoPostal, latitud, longitud);

        assertThat(mav.getViewName(), equalToIgnoringCase("lockers"));
        assertThat(mav.getModel().get("lockers"), equalTo(lockers));
    }
}
