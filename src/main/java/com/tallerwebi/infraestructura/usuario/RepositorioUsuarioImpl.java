package com.tallerwebi.infraestructura.usuario;

import com.tallerwebi.dominio.usuario.RepositorioUsuario;
import com.tallerwebi.dominio.usuario.Usuario;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Repository("repositorioUsuario")
public class RepositorioUsuarioImpl implements RepositorioUsuario {

    private final SessionFactory sessionFactory;

    @Autowired
    public RepositorioUsuarioImpl(SessionFactory sessionFactory){
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Usuario buscarUsuario(String email, String password) {
        final Session session = sessionFactory.getCurrentSession();
        return (Usuario) session.createCriteria(Usuario.class)
                .add(Restrictions.eq("email", email))
                .add(Restrictions.eq("password", password))
                .uniqueResult();
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
    public Usuario buscarUsuarioPorEmail(String email) {
        return (Usuario) sessionFactory.getCurrentSession().createCriteria(Usuario.class)
                .add(Restrictions.eq("email", email))
                .uniqueResult();
    }

    @Override
    public Usuario buscarUsuarioPorId(Long idUsuario) {
        return sessionFactory.getCurrentSession().get(Usuario.class, idUsuario);
    }

    @Override
    public Usuario buscarUsuarioPorCodigoPostal(String codigoPostal) {
        return (Usuario) sessionFactory.getCurrentSession().createCriteria(Usuario.class)
                .add(Restrictions.eq("codigoPostal", codigoPostal))
                .uniqueResult();
    }

    @Override
    public boolean existeUsuarioPorId(Long id) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "SELECT 1 FROM Usuario WHERE id = :id";
        return session.createQuery(hql)
                .setParameter("id", id)
                .uniqueResult() != null;
    }

    @Override
    public void eliminar(Long id) {
        Session session = sessionFactory.getCurrentSession();
        Usuario usuario = session.get(Usuario.class, id);
        if (usuario == null) {
            throw new EntityNotFoundException("Usuario con ID " + id + " no encontrado");
        }
        session.delete(usuario);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Usuario> listarUsuarios() {
        Criteria criteria = sessionFactory.getCurrentSession().createCriteria(Usuario.class);
        return criteria.list();
    }
}
