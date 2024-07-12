package com.tallerwebi.integracion;

import com.tallerwebi.dominio.penalizacion.PenalizacionServicioImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PenalizacionServicioImplTest {

    private PenalizacionServicioImpl penalizacionServicio;

    @BeforeEach
    public void setUp() {
        penalizacionServicio = new PenalizacionServicioImpl(null);
    }

    @Test
    public void dadoQueUsoExtendidoSinPenalizacionEntoncesDebeDevolverCero() {
        LocalDate fechaFinalizacion = LocalDate.now();
        LocalDate fechaActual = LocalDate.now();
        double penalizacion = penalizacionServicio.calcularPenalizacionPorUsoExtendido(fechaFinalizacion, fechaActual);
        assertEquals(0.0, penalizacion);
    }

    @Test
    public void dadoQueUsoExtendidoConPenalizacionEntoncesDebeCalcularPenalizacionCorrectamente() {
        LocalDate fechaFinalizacion = LocalDate.now().minusDays(5);
        LocalDate fechaActual = LocalDate.now();
        double penalizacion = penalizacionServicio.calcularPenalizacionPorUsoExtendido(fechaFinalizacion, fechaActual);
        assertEquals(500.0, penalizacion);
    }

    @Test
    public void dadoQuePagoTardioSinPenalizacionEntoncesDebeDevolverCero() {
        LocalDate fechaReserva = LocalDate.now();
        LocalDate fechaActual = LocalDate.now().plusDays(2);
        double penalizacion = penalizacionServicio.calcularPenalizacionPorPagoTardio(fechaReserva, fechaActual);
        assertEquals(0.0, penalizacion);
    }

    @Test
    public void dadoQuePagoTardioConPenalizacionEntoncesDebeCalcularPenalizacionCorrectamente() {
        LocalDate fechaReserva = LocalDate.now().minusDays(5);
        LocalDate fechaActual = LocalDate.now();
        double penalizacion = penalizacionServicio.calcularPenalizacionPorPagoTardio(fechaReserva, fechaActual);
        assertEquals(100.0, penalizacion);
    }

    @Test
    public void dadoQueMontosPorReservaEntoncesDebeDevolverMapaCorrecto() {
        List<Object[]> montosPorReserva = Arrays.asList(
                new Object[]{1L, 100.0, 50.0},
                new Object[]{2L, 200.0, null}
        );
        Map<Long, Double> expectedMap = new HashMap<>();
        expectedMap.put(1L, 150.0);
        expectedMap.put(2L, 200.0);
        Map<Long, Double> resultMap = penalizacionServicio.getLongDoubleMap(montosPorReserva);
        assertEquals(expectedMap, resultMap);
    }
}
