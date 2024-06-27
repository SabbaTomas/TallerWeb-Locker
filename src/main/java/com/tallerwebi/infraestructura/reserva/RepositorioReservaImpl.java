package com.tallerwebi.infraestructura.reserva;

import com.tallerwebi.dominio.locker.Locker;
import com.tallerwebi.dominio.reserva.RepositorioReserva;
import com.tallerwebi.dominio.reserva.Reserva;
import com.tallerwebi.dominio.usuario.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public class RepositorioReservaImpl implements RepositorioReserva {

    private final SessionFactory sessionFactory;

    public void setSession(Session session) {
    }
    public RepositorioReservaImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Usuario buscarLockerPorUsuario(String email, String password) {
        Session session = sessionFactory.getCurrentSession();
        return (Usuario) session.createQuery("FROM Usuario WHERE email = :email AND password = :password")
                .setParameter("email", email)
                .setParameter("password", password)
                .uniqueResult();
    }

    @Override
    public void guardarUsuario(Usuario usuario) {
        sessionFactory.getCurrentSession().save(usuario);
    }

    @Override
    public Usuario buscar(String email) {
        Session session = sessionFactory.getCurrentSession();
        return (Usuario) session.createQuery("FROM Usuario WHERE email = :email")
                .setParameter("email", email)
                .uniqueResult();
    }

    @Override
    public void modificar(Usuario usuario) {
        sessionFactory.getCurrentSession().update(usuario);
    }

    @Override
    public void guardarReserva(Reserva reserva) {
        sessionFactory.getCurrentSession().save(reserva);
    }

    @Override
    public Reserva obtenerReservaPorId(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Reserva.class, id);
    }

    @Override
    public void actualizarReserva(Reserva reserva) {
        sessionFactory.getCurrentSession().update(reserva);
    }

    @Override
    public void eliminarReserva(Long id) {
        Session session = sessionFactory.getCurrentSession();
        Reserva reserva = session.load(Reserva.class, id);
        if (reserva != null) {
            session.delete(reserva);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean tieneReservaActiva(Long idUsuario, Long idLocker) {
        Session session = sessionFactory.getCurrentSession();
        Query<Long> query = session.createQuery(
                "SELECT COUNT(*) FROM Reserva WHERE usuario.id = :idUsuario AND locker.id = :idLocker AND fechaFinalizacion > :hoy",
                Long.class
        );
        query.setParameter("idUsuario", idUsuario);
        query.setParameter("idLocker", idLocker);
        query.setParameter("hoy", LocalDate.now());

        Long count = query.uniqueResult();
        return count != null && count > 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = true)
    public List<Locker> obtenerLockersPorIdUsuario(Long idUsuario) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("SELECT r.locker FROM Reserva r WHERE r.usuario.id = :idUsuario")
                .setParameter("idUsuario", idUsuario)
                .list();
    }
}
