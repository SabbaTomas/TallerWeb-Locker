package com.tallerwebi.infraestructura.penalizacion;

import com.tallerwebi.dominio.penalizacion.Penalizacion;
import com.tallerwebi.dominio.penalizacion.RepositorioPenalizacion;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RepositorioPenalizacionImpl implements RepositorioPenalizacion {

    private final SessionFactory sessionFactory;

    @Autowired
    public RepositorioPenalizacionImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void guardarPenalizacion(Penalizacion penalizacion) {
        sessionFactory.getCurrentSession().save(penalizacion);
    }

    @Override
    public Penalizacion obtenerPenalizacionPorId(Long id) {
        return sessionFactory.getCurrentSession().get(Penalizacion.class, id);
    }

    @Override
    public List<Penalizacion> obtenerPenalizacionesPorReservaId(Long reservaId) {
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT p FROM Penalizacion p JOIN p.reserva r WHERE r.id = :reservaId", Penalizacion.class)
                .setParameter("reservaId", reservaId)
                .list();
    }

    @Override
    public List<Penalizacion> obtenerPenalizacionesPorUsuario(Long usuarioId) {
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT p FROM Penalizacion p JOIN p.reserva r WHERE r.usuario.id = :usuarioId", Penalizacion.class)
                .setParameter("usuarioId", usuarioId)
                .list();
    }

    @Override
    public List<Object[]> obtenerMontosPorUsuario(Long usuarioId) {
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT r.id, SUM(r.costo), SUM(p.monto) FROM Reserva r LEFT JOIN r.penalizaciones p WHERE r.usuario.id = :usuarioId GROUP BY r.id", Object[].class)
                .setParameter("usuarioId", usuarioId)
                .getResultList();
    }
}
