package com.tallerwebi.infraestructura;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.tallerwebi.dominio.usuario.Usuario;
import com.tallerwebi.infraestructura.config.HibernateTestInfraestructuraConfig;
import com.tallerwebi.infraestructura.usuario.RepositorioUsuarioImpl;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {HibernateTestInfraestructuraConfig.class})
public class RepositorioUsuarioTest {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Criteria criteria;

    @InjectMocks
    private RepositorioUsuarioImpl repositorioUsuario;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(sessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    @Rollback
    @Transactional
    public void dadoQueUsuarioExistentePuedaBuscarUsuario() {
        String email = "test@unlam.com";
        String password = "password1234";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword(password);

        when(session.createCriteria(Usuario.class)).thenReturn(criteria);
        when(criteria.add(any())).thenReturn(criteria);
        when(criteria.uniqueResult()).thenReturn(usuario);

        Usuario result = repositorioUsuario.buscarUsuario(email, password);
        assertEquals(email, result.getEmail());
        assertEquals(password, result.getPassword());
    }

    @Test
    @Rollback
    @Transactional
    public void dadoQueUsuarioPuedaGuardarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@unlam.com");

        repositorioUsuario.guardar(usuario);
        verify(session).save(usuario);
    }

    @Test
    @Rollback
    @Transactional
    public void dadoQueUsuarioExistentePuedaBuscarUsuarioPorEmail() {
        String email = "test@unlam.com";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);

        when(session.createCriteria(Usuario.class)).thenReturn(criteria);
        when(criteria.add(any())).thenReturn(criteria);
        when(criteria.uniqueResult()).thenReturn(usuario);

        Usuario result = repositorioUsuario.buscarUsuarioPorEmail(email);
        assertEquals(email, result.getEmail());
    }

    @Test
    @Rollback
    @Transactional
    public void dadoQueUsuarioNoExistenteNoSePuedaBuscarPorEmail() {
        String email = "usuario_inexistente@unlam.com";

        when(session.createCriteria(Usuario.class)).thenReturn(criteria);
        when(criteria.add(any())).thenReturn(criteria);
        when(criteria.uniqueResult()).thenReturn(null);

        Usuario result = repositorioUsuario.buscarUsuarioPorEmail(email);
        assertNull(result);
    }

    @Test
    @Rollback
    @Transactional
    public void dadoQueUsuarioExistentePuedaBuscarUsuarioPorId() {
        Long idUsuario = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(idUsuario);

        when(session.get(Usuario.class, idUsuario)).thenReturn(usuario);

        Usuario result = repositorioUsuario.buscarUsuarioPorId(idUsuario);
        assertEquals(idUsuario, result.getId());
    }

    @Test
    @Rollback
    @Transactional
    public void dadoQueUsuarioExistentePuedaBuscarUsuarioPorCodigoPostal() {
        String codigoPostal = "1704";
        Usuario usuario = new Usuario();
        usuario.setCodigoPostal(codigoPostal);

        when(session.createCriteria(Usuario.class)).thenReturn(criteria);
        when(criteria.add(any())).thenReturn(criteria);
        when(criteria.uniqueResult()).thenReturn(usuario);

        Usuario result = repositorioUsuario.buscarUsuarioPorCodigoPostal(codigoPostal);
        assertEquals(codigoPostal, result.getCodigoPostal());
    }

    @Test
    @Rollback
    @Transactional
    public void dadoQueUsuarioExistentePuedaModificarUsuario() {
        long id = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setEmail("test@unlam.com");

        String nuevoEmail = "nuevo_email@unlam.com";
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(id);
        usuarioActualizado.setEmail(nuevoEmail);

        repositorioUsuario.modificar(usuarioActualizado);

        verify(session).update(usuarioActualizado);
    }

    @Test
    @Rollback
    @Transactional
    public void dadoQueUsuarioExistentePuedaEliminarUsuario() {
        long id = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setEmail("test@unlam.com");

        when(session.get(Usuario.class, id)).thenReturn(usuario);

        repositorioUsuario.eliminar(id);

        verify(session).delete(usuario);
    }

    @Test
    @Rollback
    @Transactional
    public void dadoQueUsuarioNoExistenteNoSePuedaEliminarUsuario() {
        long id = 999L;

        when(session.get(Usuario.class, id)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> {
            repositorioUsuario.eliminar(id);
        });

        verify(session, never()).delete(any());
    }

    @Test
    @Rollback
    @Transactional
    public void dadoQueUsuarioExistentePuedaVerLaListaUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(new Usuario("usuario1@unlam.com", "password1234"));
        usuarios.add(new Usuario("usuario2@unlam.com", "password1234"));

        Criteria criteria = mock(Criteria.class);
        when(session.createCriteria(Usuario.class)).thenReturn(criteria);
        when(criteria.list()).thenReturn(usuarios);

        List<Usuario> resultado = repositorioUsuario.listarUsuarios();

        assertEquals(2, resultado.size());
        assertEquals("usuario1@unlam.com", resultado.get(0).getEmail());
        assertEquals("usuario2@unlam.com", resultado.get(1).getEmail());
    }
}
