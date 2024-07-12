package com.tallerwebi.integracion;

import com.tallerwebi.dominio.locker.Locker;
import com.tallerwebi.dominio.locker.RepositorioDatosLocker;
import com.tallerwebi.dominio.locker.ServicioLocker;
import com.tallerwebi.infraestructura.locker.RepositorioDatosLockerImpl;
import com.tallerwebi.dominio.locker.ServicioLockerImpl;
import com.tallerwebi.dominio.locker.excepciones.LockerNoEncontrado;
import com.tallerwebi.dominio.locker.excepciones.ParametrosDelLockerInvalidos;
import com.tallerwebi.dominio.locker.Enum.TipoLocker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServicioLockerTest {

    private ServicioLocker servicioLocker;
    private RepositorioDatosLocker repositorioDatosLocker;

    @BeforeEach
    public void init() {
        repositorioDatosLocker = mock(RepositorioDatosLockerImpl.class);
        servicioLocker = new ServicioLockerImpl(repositorioDatosLocker);
    }

    // Test de creación de Locker
    @Test
    public void dadoQueElLockerEsValidoEntoncesCrearLockerDebeGuardarElLocker() throws ParametrosDelLockerInvalidos {
        // Preparación
        Locker locker = new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "1704");

        // Ejecución
        servicioLocker.crearLocker(locker);

        // Verificación
        verify(repositorioDatosLocker, times(1)).guardar(locker);
    }

    // Test de actualización de Locker
    @Test
    public void dadoQueElLockerExisteEntoncesActualizarLockerDebeActualizarElLocker() throws LockerNoEncontrado, ParametrosDelLockerInvalidos {
        // Preparación
        Long idLocker = 1L;
        TipoLocker tipoInicial = TipoLocker.PEQUENIO;
        TipoLocker nuevoTipo = TipoLocker.GRANDE;
        Locker locker = new Locker(tipoInicial, 40.7128, -74.0060, "1704");
        locker.setId(idLocker);

        when(repositorioDatosLocker.obtenerLockerPorId(idLocker)).thenReturn(locker);

        // Ejecución
        servicioLocker.actualizarLocker(idLocker, nuevoTipo);

        // Verificación
        verify(repositorioDatosLocker, times(1)).actualizar(idLocker, nuevoTipo);
        assertEquals(nuevoTipo, locker.getTipo());
    }

    // Test de actualización de Locker con tipo nulo
    @Test
    public void dadoQueElTipoDeLockerEsNuloEntoncesActualizarLockerDebeLanzarExcepcion() throws LockerNoEncontrado, ParametrosDelLockerInvalidos {
        // Preparación
        Long idLocker = 1L;
        TipoLocker tipoLocker = null;

        // Ejecución y Verificación
        ParametrosDelLockerInvalidos exception = assertThrows(ParametrosDelLockerInvalidos.class, () -> {
            servicioLocker.actualizarLocker(idLocker, tipoLocker);
        });

        assertEquals("Tipo de locker no puede ser nulo", exception.getMessage());
    }

    // Test de eliminación de Locker
    @Test
    public void dadoQueElLockerExisteEntoncesEliminarLockerDebeEliminarElLocker() throws ParametrosDelLockerInvalidos {
        // Preparación
        Long idLocker = 1L;

        // Ejecución
        servicioLocker.eliminarLocker(idLocker);

        // Verificación
        verify(repositorioDatosLocker, times(1)).eliminar(idLocker);
    }

    // Test de obtención de Lockers por tipo
    @Test
    public void dadoQueExistenLockersDelTipoEspecificadoEntoncesObtenerLockersPorTipoDebeRetornarEsosLockers() throws ParametrosDelLockerInvalidos {
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

    // Test de obtención de Locker por ID
    @Test
    public void dadoQueElLockerConIdEspecificadoExisteEntoncesObtenerLockerPorIdDebeRetornarEseLocker() throws ParametrosDelLockerInvalidos {
        // Preparación
        Long idLocker = 1L;
        Locker locker = new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "1704");
        locker.setId(idLocker);

        when(repositorioDatosLocker.obtenerLockerPorId(idLocker)).thenReturn(locker);

        // Ejecución
        Locker resultado = servicioLocker.obtenerLockerPorId(idLocker);

        // Verificación
        assertEquals(locker, resultado);
    }

    // Test de obtención de Lockers por código postal
    @Test
    public void dadoQueExistenLockersConElCodigoPostalEspecificadoEntoncesObtenerLockersPorCodigoPostalDebeRetornarEsosLockers() throws ParametrosDelLockerInvalidos {
        // Preparación
        String codigoPostal = "1704";
        List<Locker> lockers = Arrays.asList(
                new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, codigoPostal),
                new Locker(TipoLocker.MEDIANO, 40.7129, -74.0061, codigoPostal)
        );

        when(repositorioDatosLocker.obtenerLockersPorCodigoPostal(codigoPostal)).thenReturn(lockers);

        // Ejecución
        List<Locker> resultado = servicioLocker.obtenerLockersPorCodigoPostal(codigoPostal);

        // Verificación
        assertEquals(lockers, resultado);
    }

    // Test de obtención de Lockers seleccionados
    @Test
    public void dadoQueExistenLockersSeleccionadosEntoncesObtenerLockersSeleccionadosDebeRetornarEsosLockers() {
        // Preparación
        List<Locker> lockers = Arrays.asList(
                new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "1704"),
                new Locker(TipoLocker.GRANDE, 40.7129, -74.0061, "1704")
        );

        when(repositorioDatosLocker.obtenerSeleccionados()).thenReturn(lockers);

        // Ejecución
        List<Locker> resultado = servicioLocker.obtenerLockersSeleccionados();

        // Verificación
        assertEquals(lockers, resultado);
    }

    // Test de eliminación de todos los Lockers
    @Test
    public void dadoQueExistenLockersEntoncesEliminarTodosDebeEliminarTodosLosLockers() {
        // Ejecución
        servicioLocker.eliminarTodos();

        // Verificación
        verify(repositorioDatosLocker, times(1)).eliminarTodos();
    }

    // Test del camino no feliz: creación de Locker con parámetros nulos
    @Test
    public void dadoQueLosParametrosDelLockerSonNulosEntoncesCrearLockerDebeLanzarExcepcion() throws ParametrosDelLockerInvalidos {
        // Preparación
        Locker locker = new Locker();

        // Ejecución y Verificación
        ParametrosDelLockerInvalidos exception = assertThrows(ParametrosDelLockerInvalidos.class, () -> {
            servicioLocker.crearLocker(locker);
        });

        assertEquals("Locker no puede tener parámetros nulos", exception.getMessage());
    }

    // Test del camino no feliz: obtención de Lockers por tipo inválido
    @Test
    public void dadoQueElTipoDeLockerEsInvalidoEntoncesObtenerLockersPorTipoDebeLanzarExcepcion() throws ParametrosDelLockerInvalidos {
        // Preparación
        TipoLocker tipoLocker = null;

        // Ejecución y Verificación
        ParametrosDelLockerInvalidos exception = assertThrows(ParametrosDelLockerInvalidos.class, () -> {
            servicioLocker.obtenerLockersPorTipo(tipoLocker);
        });

        assertEquals("Tipo de locker no puede ser nulo", exception.getMessage());
    }

    // Test del camino no feliz: eliminación de Locker con ID inválido
    @Test
    public void dadoQueElIdDeLockerEsInvalidoEntoncesEliminarLockerDebeLanzarExcepcion() throws ParametrosDelLockerInvalidos {
        // Preparación
        Long idLocker = -1L;

        doThrow(new ParametrosDelLockerInvalidos("ID de locker inválido")).when(repositorioDatosLocker).eliminar(idLocker);

        // Ejecución y Verificación
        ParametrosDelLockerInvalidos exception = assertThrows(ParametrosDelLockerInvalidos.class, () -> {
            servicioLocker.eliminarLocker(idLocker);
        });

        assertEquals("ID de locker inválido", exception.getMessage());
    }

    // Test del camino no feliz: obtención de Lockers por código postal inválido
    @Test
    public void dadoQueElCodigoPostalEsInvalidoEntoncesObtenerLockersPorCodigoPostalDebeLanzarExcepcion() throws ParametrosDelLockerInvalidos {
        // Preparación
        String codigoPostal = null;

        // Ejecución y Verificación
        ParametrosDelLockerInvalidos exception = assertThrows(ParametrosDelLockerInvalidos.class, () -> {
            servicioLocker.obtenerLockersPorCodigoPostal(codigoPostal);
        });

        assertEquals("Código postal no puede ser nulo o vacío", exception.getMessage());
    }

    // Test del camino no feliz: actualización de Locker que no existe
    @Test
    public void dadoQueElLockerNoExisteEntoncesActualizarLockerDebeLanzarExcepcion() throws LockerNoEncontrado, ParametrosDelLockerInvalidos {
        // Preparación
        Long idLocker = -1L;
        TipoLocker nuevoTipo = TipoLocker.GRANDE;

        when(repositorioDatosLocker.obtenerLockerPorId(idLocker)).thenReturn(null);

        // Ejecución y Verificación
        LockerNoEncontrado exception = assertThrows(LockerNoEncontrado.class, () -> {
            servicioLocker.actualizarLocker(idLocker, nuevoTipo);
        });

        assertEquals("No se encontró ningún locker con el ID proporcionado: " + idLocker, exception.getMessage());
    }

    // Test del camino no feliz: obtención de Lockers con repositorio vacío
    @Test
    public void dadoQueElRepositorioEstaVacioEntoncesObtenerLockersSeleccionadosDebeRetornarListaVacia() {
        // Preparación
        when(repositorioDatosLocker.obtenerSeleccionados()).thenReturn(new ArrayList<>());

        // Ejecución
        List<Locker> resultado = servicioLocker.obtenerLockersSeleccionados();

        // Verificación
        assertTrue(resultado.isEmpty());
    }

    // Test de búsqueda de Lockers por código postal
    @Test
    public void dadoQueExistenLockersConElCodigoPostalEspecificadoEntoncesBuscarLockersPorCodigoPostalDebeRetornarEsosLockers() throws ParametrosDelLockerInvalidos {
        String codigoPostal = "1704";
        List<Locker> lockers = List.of(new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, codigoPostal));

        when(servicioLocker.obtenerLockersPorCodigoPostal(codigoPostal)).thenReturn(lockers);

        List<Locker> resultado = servicioLocker.buscarLockers(codigoPostal, null, null, null);
        assertEquals(lockers, resultado);
    }

    // Test de búsqueda de Lockers por coordenadas
    @Test
    public void dadoQueExistenLockersCercanosALasCoordenadasEspecificadasEntoncesBuscarLockersPorCoordenadasDebeRetornarEsosLockers() throws ParametrosDelLockerInvalidos {
        double latitud = 40.7128;
        double longitud = -74.0060;
        double radio = 5.0;
        List<Locker> lockers = List.of(new Locker(TipoLocker.PEQUENIO, latitud, longitud, "1704"));

        when(servicioLocker.obtenerLockersCercanos(latitud, longitud, radio)).thenReturn(lockers);

        List<Locker> resultado = servicioLocker.buscarLockers(null, latitud, longitud, radio);
        assertEquals(lockers, resultado);
    }

    // Test de búsqueda de Lockers sin parámetros, usando alternativos
    @Test
    public void dadoQueNoSeProporcionanParametrosEntoncesBuscarLockersDebeRetornarLockersSeleccionados() {
        List<Locker> lockersSeleccionados = List.of(new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "1704"));

        when(servicioLocker.obtenerLockersSeleccionados()).thenReturn(lockersSeleccionados);

        List<Locker> resultado = servicioLocker.buscarLockers(null, null, null, null);
        assertEquals(lockersSeleccionados, resultado);
    }
}
