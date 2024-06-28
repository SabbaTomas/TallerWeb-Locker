package com.tallerwebi.infraestructura;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import com.tallerwebi.dominio.usuario.Usuario;
import com.tallerwebi.infraestructura.usuario.RepositorioUsuarioImpl;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class RepositorioUsuarioTest {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @Mock
    private Criteria criteria;

    @InjectMocks
    private RepositorioUsuarioImpl repositorioUsuario;

    private LocalValidatorFactoryBean validator;

    @BeforeEach
    public void setUp() {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    public void queSePuedaBuscarUsuario() {
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
    public void queSePuedaGuardarUsuario() {
        Usuario usuario = new Usuario();
        usuario.setEmail("test@unlam.com");

        repositorioUsuario.guardar(usuario);
        verify(session).save(usuario);
    }

    @Test
    public void queSePuedaBuscarUsuarioPorEmail() {
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
    public void queNoSePuedaBuscarPorEmailSiNoExiste() {
        String email = "usuario_inexistente@unlam.com";

        when(session.createCriteria(Usuario.class)).thenReturn(criteria);
        when(criteria.add(any())).thenReturn(criteria);
        when(criteria.uniqueResult()).thenReturn(null);

        Usuario result = repositorioUsuario.buscarUsuarioPorEmail(email);
        assertNull(result);
    }

    @Test
    public void queSePuedaBuscarUsuarioPorId() {
        Long idUsuario = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(idUsuario);

        when(session.get(Usuario.class, idUsuario)).thenReturn(usuario);

        Usuario result = repositorioUsuario.buscarUsuarioPorId(idUsuario);
        assertEquals(idUsuario, result.getId());
    }

    @Test
    public void queSePuedaBuscarUsuarioPorCodigoPostal() {
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
    public void queSePuedaModificarUsuario() {
        //preparacion
        long id = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setEmail("test@unlam.com");

        // ejecucion
        String nuevoEmail = "nuevo_email@unlam.com";
        Usuario usuarioActualizado = new Usuario();
        usuarioActualizado.setId(id);
        usuarioActualizado.setEmail(nuevoEmail);
        // llamar al metodo modificar del repositorio
        repositorioUsuario.modificar(usuarioActualizado);

        // verificacion
        verify(session).update(usuarioActualizado);
    }

    @Test
    public void queSePuedaEliminarUsuario() {
        long id = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setEmail("test@unlam.com");

        when(session.get(Usuario.class, id)).thenReturn(usuario);

        repositorioUsuario.eliminar(id);

        verify(session).delete(usuario);
    }

    @Test
    public void queNoSePuedaEliminarUsuarioNoExistente() {
        long id = 999L;

        when(session.get(Usuario.class, id)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> {
            repositorioUsuario.eliminar(id);
        });

        verify(session, never()).delete(any());
    }

    @Test
    public void queSePuedaVerLaListaUsuarios() {
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
