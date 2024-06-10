package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.RepositorioReservaImpl;
import com.tallerwebi.dominio.Usuario;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class RepositorioReservaTest {

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @InjectMocks
    private RepositorioReservaImpl repoReserva;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(sessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    public void testBuscarLocker() {
        // Given
        String email = "test@example.com";
        String password = "password";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword(password);
        Query query = mock(Query.class);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.uniqueResult()).thenReturn(usuario);

        // When
        Usuario foundUsuario = repoReserva.buscarLocker(email, password);

        // Then
        assertNotNull(foundUsuario);
        assertEquals(email, foundUsuario.getEmail());
        verify(session).createQuery(anyString());
        verify(query, times(2)).setParameter(anyString(), any());
        verify(query).uniqueResult();
    }

    @Test
    public void testGuardar() {
        // Given
        Usuario usuario = new Usuario();

        // When
        repoReserva.guardar(usuario);

        // Then
        verify(session).save(usuario);
    }
    @Test
    public void testBuscarLockerUsuarioNoExiste() {
        // Given
        String email = "noexistente@example.com";
        String password = "nopassword";
        Query query = mock(Query.class);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.uniqueResult()).thenReturn(null);

        // When
        Usuario foundUsuario = repoReserva.buscarLocker(email, password);

        // Then
        assertNull(foundUsuario);
    }

    @Test
    public void testBuscarUsuarioNoExiste() {
        // Given
        String email = "noexistente@example.com";
        Query query = mock(Query.class);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.uniqueResult()).thenReturn(null);

        // When
        Usuario foundUsuario = repoReserva.buscar(email);

        // Then
        assertNull(foundUsuario);
    }

    @Test
    public void testModificarUsuarioExistente() {
        // Given
        Usuario usuario = new Usuario();
        usuario.setEmail("existing@example.com");
        usuario.setPassword("existingpassword");

        // When
        repoReserva.modificar(usuario);

        // Then
        verify(session).update(usuario);
    }
    // Similar tests for other methods like buscar, modificar, and the methods related to Reserva management


}
