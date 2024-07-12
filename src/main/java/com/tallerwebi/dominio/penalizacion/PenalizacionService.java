package com.tallerwebi.dominio.penalizacion;

import com.tallerwebi.dominio.reserva.Reserva;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Transactional
public interface PenalizacionService {
    double calcularPenalizacionPorPagoTardio(LocalDate fechaReserva, LocalDate fechaPago);
    double calcularPenalizacionPorUsoExtendido(LocalDate fechaFinalizacion, LocalDate fechaActual);

    void registrarPenalizacion(Reserva reserva, double monto);

    Penalizacion obtenerPenalizacionPorId(Long id);

    List<Penalizacion> obtenerPenalizacionesPorUsuario(Long usuarioId);

    List<Penalizacion> obtenerPenalizacionesPorReservaId(Long reservaId);

    List<Object[]> MontosPorUsuario(Long usuarioId);

    Map<Long, Double> getLongDoubleMap(List<Object[]> montosPorReserva);
}
