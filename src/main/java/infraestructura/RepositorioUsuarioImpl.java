package infraestructura;

import dominio.locker.RepositorioUsuario;
import dominio.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("repositorioUsuario")
public class RepositorioUsuarioImpl implements RepositorioUsuario {

    private SessionFactory sessionFactory;

    @Autowired
    public RepositorioUsuarioImpl(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Usuario buscarUsuario(String email, String password) {
        final Session session = sessionFactory.getCurrentSession();
        String hql = "FROM Usuario WHERE email = :email AND password = :password";
        Usuario usuario = null;
        try {
            usuario = (Usuario) session.createQuery(hql)
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (usuario == null) {
            System.out.println("Usuario no encontrado para email: " + email + " y password: " + password);
        } else {
            System.out.println("Usuario encontrado: " + usuario.getEmail());
        }
        return usuario;
    }

    @Override
    public Usuario buscarUsuarioPorId(Long id) {
        return sessionFactory.getCurrentSession().get(Usuario.class, id);
    }

    @Override
    public void guardar(Usuario usuario) {
        sessionFactory.getCurrentSession().save(usuario);
    }

    @Override
    public Usuario buscar(String email) {
        return (Usuario) sessionFactory.getCurrentSession().createCriteria(Usuario.class)
                .add(Restrictions.eq("email", email))
                .uniqueResult();
    }

    @Override
    public void modificar(Usuario usuario) {
        sessionFactory.getCurrentSession().update(usuario);
    }

    @Override
    public boolean existeUsuarioPorId(Long id) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT 1 FROM Usuario WHERE id = :id";
        return session.createQuery(hql)
                .setParameter("id", id)
                .uniqueResult() != null;
    }
}
