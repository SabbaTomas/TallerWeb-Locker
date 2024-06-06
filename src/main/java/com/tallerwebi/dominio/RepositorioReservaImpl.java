package com.tallerwebi.dominio;
import com.tallerwebi.dominio.reserva.RepositorioReserva;
import com.tallerwebi.dominio.reserva.Reserva;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;

@Repository
@Transactional
public class RepositorioReservaImpl implements RepositorioReserva {

    private final SessionFactory sessionFactory;

    public RepositorioReservaImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Usuario buscarLocker(String email, String password) {
        Session session = sessionFactory.getCurrentSession();
        return (Usuario) session.createQuery("FROM Usuario WHERE email = :email AND password = :password")
                .setParameter("email", email)
                .setParameter("password", password)
                .uniqueResult();
    }

    @Override
    public void guardar(Usuario usuario) {
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

    // Métodos adicionales para gestionar las reservas
    public void guardarReserva(Reserva reserva) {
        sessionFactory.getCurrentSession().save(reserva);
    }

    public Reserva obtenerReservaPorId(Long id) {
        return (Reserva) sessionFactory.getCurrentSession()
                .createQuery("FROM Reserva WHERE id = :id")
                .setParameter("id", id)
                .uniqueResult();
    }

    public void actualizarReserva(Reserva reserva) {
        sessionFactory.getCurrentSession().update(reserva);
    }

    public void eliminarReserva(Long id) {
        Session session = sessionFactory.getCurrentSession();
        Reserva reserva = session.load(Reserva.class, id);
        if (reserva != null) {
            session.delete(reserva);
        }
    }
}
