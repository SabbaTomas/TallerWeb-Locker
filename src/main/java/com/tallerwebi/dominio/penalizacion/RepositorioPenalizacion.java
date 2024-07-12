package com.tallerwebi.dominio.penalizacion;

import com.tallerwebi.dominio.reserva.Reserva;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface RepositorioPenalizacion {
    void guardarPenalizacion(Penalizacion penalizacion);

    Penalizacion obtenerPenalizacionPorId(Long id);

    List<Penalizacion> obtenerPenalizacionesPorReservaId(Long reservaId);

    List<Penalizacion> obtenerPenalizacionesPorUsuario(Long usuarioId);

    List<Object[]> obtenerMontosPorUsuario(Long usuarioId);
}
