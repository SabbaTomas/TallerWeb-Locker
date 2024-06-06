package com.tallerwebi.integracion;

import com.tallerwebi.dominio.Locker;
import com.tallerwebi.dominio.RepositorioDatosLockerImpl;
import com.tallerwebi.dominio.ServicioLockerImpl;
import com.tallerwebi.dominio.locker.TipoLocker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServicioLockerTest {

    private ServicioLockerImpl servicioLocker;
    private RepositorioDatosLockerImpl repositorioDatosLocker;

    @BeforeEach
    public void setUp() {
        repositorioDatosLocker = mock(RepositorioDatosLockerImpl.class);
        servicioLocker = new ServicioLockerImpl(repositorioDatosLocker);
    }

    @Test
    public void queSePuedaCrearUnLocker() {
        // Preparación
        Locker locker = new Locker(TipoLocker.PEQUEÑO, 40.7128, -74.0060, "1704");

        // Ejecución
        servicioLocker.crearLocker(locker);

        // Verificación
        verify(repositorioDatosLocker, times(1)).guardar(locker);
    }

    @Test
    public void queSePuedaActualizarUnLocker() {
        // Preparación
        Long idLocker = 1L;
        TipoLocker nuevoTipo = TipoLocker.GRANDE;
        Locker locker = new Locker(TipoLocker.PEQUEÑO, 40.7128, -74.0060, "1704");
        locker.setId(idLocker);

        when(repositorioDatosLocker.obtenerPorId(idLocker)).thenReturn(locker);

        // Ejecución
        servicioLocker.actualizarLocker(idLocker, nuevoTipo);

        // Verificación
        verify(repositorioDatosLocker, times(1)).actualizar(locker);
        assertEquals(nuevoTipo, locker.getTipo());
    }

    @Test
    public void queNoSePuedaActualizarLockerConIdInvalido() {
        // Preparación
        Long idLocker = -1L;
        TipoLocker nuevoTipo = TipoLocker.GRANDE;

        when(repositorioDatosLocker.obtenerPorId(idLocker)).thenReturn(null);

        // Ejecución y Verificación
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            servicioLocker.actualizarLocker(idLocker, nuevoTipo);
        });

        assertEquals("No se encontró ningún locker con el ID proporcionado: " + idLocker, exception.getMessage());
    }


    @Test
    public void queSePuedaEliminarUnLocker() {
        // Preparación
        Long idLocker = 1L;

        // Ejecución
        servicioLocker.eliminarLocker(idLocker);

        // Verificación
        verify(repositorioDatosLocker, times(1)).eliminar(idLocker);
    }

    @Test
    public void queSePuedanObtenerLockersPorTipo() {
        // Preparación
        TipoLocker tipoLocker = TipoLocker.MEDIANO;
        List<Locker> lockers = Arrays.asList(
                new Locker(tipoLocker, 40.7128, -74.0060, "1704"),
                new Locker(tipoLocker, 40.7129, -74.0061, "1704")
        );

        when(repositorioDatosLocker.obtenerLockersPorTipo(tipoLocker)).thenReturn(lockers);

        // Ejecución
        List<Locker> resultado = servicioLocker.obtenerLockersPorTipo(tipoLocker);

        // Verificación
        assertEquals(lockers, resultado);
    }

    @Test
    public void queSePuedaObtenerLockerPorId() {
        // Preparación
        Long idLocker = 1L;
        Locker locker = new Locker(TipoLocker.PEQUEÑO, 40.7128, -74.0060, "1704");
        locker.setId(idLocker);

        when(repositorioDatosLocker.obtenerPorId(idLocker)).thenReturn(locker);

        // Ejecución
        Locker resultado = servicioLocker.obtenerLockerPorId(idLocker);

        // Verificación
        assertEquals(locker, resultado);
    }

    @Test
    public void queSePuedanObtenerLockersPorCodigoPostal() {
        // Preparación
        String codigoPostal = "1704";
        List<Locker> lockers = Arrays.asList(
                new Locker(TipoLocker.PEQUEÑO, 40.7128, -74.0060, codigoPostal),
                new Locker(TipoLocker.MEDIANO, 40.7129, -74.0061, codigoPostal)
        );

        when(repositorioDatosLocker.obtenerLockersPorCodigoPostal(codigoPostal)).thenReturn(lockers);

        // Ejecución
        List<Locker> resultado = servicioLocker.obtenerLockersPorCodigoPostal(codigoPostal);

        // Verificación
        assertEquals(lockers, resultado);
    }

    @Test
    public void queSePuedanObtenerLockersSeleccionados() {
        // Preparación
        List<Locker> lockers = Arrays.asList(
                new Locker(TipoLocker.PEQUEÑO, 40.7128, -74.0060, "1704"),
                new Locker(TipoLocker.GRANDE, 40.7129, -74.0061, "1704")
        );

        when(repositorioDatosLocker.obtenerSeleccionados()).thenReturn(lockers);

        // Ejecución
        List<Locker> resultado = servicioLocker.obtenerLockersSeleccionados();

        // Verificación
        assertEquals(lockers, resultado);
    }

    @Test
    public void queSePuedanEliminarTodosLosLockers() {
        // Ejecución
        servicioLocker.eliminarTodos();

        // Verificación
        verify(repositorioDatosLocker, times(1)).eliminarTodos();
    }

    // Nuevos tests del camino no feliz

    @Test
    public void queNoSePuedaCrearLockerConParametrosNulos() {
        // Preparación
        Locker locker = new Locker();

        // Ejecución y Verificación
        assertThrows(IllegalArgumentException.class, () -> {
            servicioLocker.crearLocker(locker);
        });
    }

    @Test
    public void queNoSePuedanObtenerLockersPorTipoInvalido() {
        // Preparación
        TipoLocker tipoLocker = null;

        // Ejecución y Verificación
        assertThrows(IllegalArgumentException.class, () -> {
            servicioLocker.obtenerLockersPorTipo(tipoLocker);
        });
    }

    @Test
    public void queNoSePuedaEliminarLockerConIdInvalido() {
        // Preparación
        Long idLocker = -1L;

        doThrow(new IllegalArgumentException("ID de locker inválido")).when(repositorioDatosLocker).eliminar(idLocker);

        // Ejecución y Verificación
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            servicioLocker.eliminarLocker(idLocker);
        });

        assertEquals("ID de locker inválido", exception.getMessage());
    }

    @Test
    public void queNoSePuedanObtenerLockersPorCodigoPostalInvalido() {
        // Preparación
        String codigoPostal = null;

        // Ejecución y Verificación
        assertThrows(IllegalArgumentException.class, () -> {
            servicioLocker.obtenerLockersPorCodigoPostal(codigoPostal);
        });
    }

    @Test
    public void queNoSePuedanObtenerLockersConRepositorioVacio() {
        // Preparación
        when(repositorioDatosLocker.obtenerSeleccionados()).thenReturn(new ArrayList<>());

        // Ejecución
        List<Locker> resultado = servicioLocker.obtenerLockersSeleccionados();

        // Verificación
        assertTrue(resultado.isEmpty());
    }

    @Test
    public void obtenerLockersPorCodigoPostal_Exitoso() {
        String codigoPostal = "1704";
        List<Locker> lockers = Arrays.asList(new Locker(TipoLocker.PEQUEÑO, 40.7128, -74.0060, codigoPostal));

        when(repositorioDatosLocker.obtenerLockersPorCodigoPostal(codigoPostal)).thenReturn(lockers);

        List<Locker> resultado = servicioLocker.obtenerLockersPorCodigoPostal(codigoPostal);
        assertEquals(lockers, resultado);
    }

    @Test
    public void buscarLockers_PorCodigoPostal() {
        String codigoPostal = "1704";
        List<Locker> lockers = Arrays.asList(new Locker(TipoLocker.PEQUEÑO, 40.7128, -74.0060, codigoPostal));

        when(servicioLocker.obtenerLockersPorCodigoPostal(codigoPostal)).thenReturn(lockers);

        List<Locker> resultado = servicioLocker.buscarLockers(codigoPostal, null, null, null);
        assertEquals(lockers, resultado);
    }

    @Test
    public void buscarLockers_PorCoordenadas() {
        Double latitud = 40.7128;
        Double longitud = -74.0060;
        Double radio = 5.0;
        List<Locker> lockers = Arrays.asList(new Locker(TipoLocker.PEQUEÑO, latitud, longitud, "1704"));

        when(servicioLocker.obtenerLockersCercanos(latitud, longitud, radio)).thenReturn(lockers);

        List<Locker> resultado = servicioLocker.buscarLockers(null, latitud, longitud, radio);
        assertEquals(lockers, resultado);
    }

    @Test
    public void buscarLockers_SinParametrosUsaAlternativos() {
        List<Locker> lockersSeleccionados = Arrays.asList(new Locker(TipoLocker.PEQUEÑO, 40.7128, -74.0060, "1704"));

        when(servicioLocker.obtenerLockersSeleccionados()).thenReturn(lockersSeleccionados);

        List<Locker> resultado = servicioLocker.buscarLockers(null, null, null, null);
        assertEquals(lockersSeleccionados, resultado);
    }

}
