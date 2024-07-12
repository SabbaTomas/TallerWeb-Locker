package com.tallerwebi.dominio.penalizacion;

import com.tallerwebi.dominio.reserva.Reserva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class PenalizacionServicioImpl implements PenalizacionService {

    private final RepositorioPenalizacion repositorioPenalizacion;

    @Autowired
    public PenalizacionServicioImpl(RepositorioPenalizacion repositorioPenalizacion) {
        this.repositorioPenalizacion = repositorioPenalizacion;
    }

    @Override
    public double calcularPenalizacionPorUsoExtendido(LocalDate fechaFinalizacion, LocalDate fechaActual) {
        long diasExtendidos = DAYS.between(fechaFinalizacion, fechaActual);
        if (diasExtendidos > 0) {
            return diasExtendidos * 100.0;
        }
        return 0.0;
    }

    @Override
    public double calcularPenalizacionPorPagoTardio(LocalDate fechaReserva, LocalDate fechaActual) {
        long diasTardios = DAYS.between(fechaReserva.plusDays(3), fechaActual);
        if (diasTardios > 0) {
            return diasTardios * 50.0;
        }
        return 0.0;
    }

    @Override
    public void registrarPenalizacion(Reserva reserva, double monto) {
        if (reserva != null && reserva.getLocker() != null) {
            Penalizacion penalizacion = new Penalizacion();
            penalizacion.setReserva(reserva);
            penalizacion.setMonto(monto);
            if (reserva.getLocker().getDescripcion() != null) {
                penalizacion.setDescripcion("Penalización por uso extendido o pago tardío para el locker: " + reserva.getLocker().getDescripcion());
            } else {
                penalizacion.setDescripcion("Penalización por uso extendido o pago tardío");
            }
            repositorioPenalizacion.guardarPenalizacion(penalizacion);
        } else {
            System.out.println("Error: La reserva o el locker son nulos");
        }
    }

    @Override
    public Penalizacion obtenerPenalizacionPorId(Long id) {
        return repositorioPenalizacion.obtenerPenalizacionPorId(id);
    }

    @Override
    public List<Penalizacion> obtenerPenalizacionesPorUsuario(Long usuarioId) {
        return repositorioPenalizacion.obtenerPenalizacionesPorUsuario(usuarioId);
    }

    @Override
    public List<Penalizacion> obtenerPenalizacionesPorReservaId(Long reservaId) {
        return repositorioPenalizacion.obtenerPenalizacionesPorReservaId(reservaId);
    }

    @Override
    public List<Object[]> MontosPorUsuario(Long usuarioId) {
        return repositorioPenalizacion.obtenerMontosPorUsuario(usuarioId);
    }

    @Override
    public Map<Long, Double> getLongDoubleMap(List<Object[]> montosPorReserva) {
        Map<Long, Double> montoTotalPorReserva = new HashMap<>();
        for (Object[] resultado : montosPorReserva) {
            Long reservaId = (Long) resultado[0];
            Double montoReserva = (Double) resultado[1];
            Double montoPenalizacion = (Double) resultado[2];
            montoTotalPorReserva.put(reservaId, montoReserva + (montoPenalizacion != null ? montoPenalizacion : 0));
        }
        return montoTotalPorReserva;
    }

}
