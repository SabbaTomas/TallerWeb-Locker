package infraestructura;

import dominio.RepositorioReservaImpl;
import dominio.Usuario;
import dominio.reserva.Reserva;
import infraestructura.config.HibernateTestInfraestructuraConfig;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.time.LocalDate;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {HibernateTestInfraestructuraConfig.class})
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
        repoReserva.setSession(session);
    }

    @Test
    @Rollback
    @Transactional
    public void testBuscarLockerPorUsuario() {
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
        Usuario foundUsuario = repoReserva.buscarLockerPorUsuario(email, password);

        // Then
        assertNotNull(foundUsuario);
        assertEquals(email, foundUsuario.getEmail());
        verify(session).createQuery(anyString());
        verify(query, times(2)).setParameter(anyString(), any());
        verify(query).uniqueResult();
    }

    @Test
    @Rollback
    @Transactional
    public void testGuardarUsuario() {
        // Given
        Usuario usuario = new Usuario();

        // When
        repoReserva.guardarUsuario(usuario);

        // Then
        verify(session).save(usuario);
    }

    @Test
    @Rollback
    @Transactional
    public void testBuscarLockerUsuarioNoExiste() {
        // Given
        String email = "noexistente@example.com";
        String password = "nopassword";
        Query query = mock(Query.class);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.uniqueResult()).thenReturn(null);

        // When
        Usuario foundUsuario = repoReserva.buscarLockerPorUsuario(email, password);

        // Then
        assertNull(foundUsuario);
    }

    @Test
    @Rollback
    @Transactional
    public void testBuscarUsuario() {
        // Given
        String email = "existing@example.com";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        Query query = mock(Query.class);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.uniqueResult()).thenReturn(usuario);

        // When
        Usuario foundUsuario = repoReserva.buscar(email);

        // Then
        assertNotNull(foundUsuario);
        assertEquals(email, foundUsuario.getEmail());
    }

    @Test
    @Rollback
    @Transactional
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
    @Rollback
    @Transactional
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

    @Test
    @Transactional
    @Rollback
    public void testGuardarReserva() {
        // Given
        Reserva reserva = new Reserva();
        reserva.setFechaReserva(LocalDate.of(2024, 6, 1));
        reserva.setFechaFinalizacion(LocalDate.of(2024, 6, 10));

        // When
        repoReserva.guardarReserva(reserva);

        // Then
        verify(session).save(reserva);
    }

    @Test
    @Transactional
    @Rollback
    public void testObtenerReservaPorId() {
        // Given
        Long id = 1L;
        Reserva reserva = new Reserva();
        reserva.setId(id);
        reserva.setFechaReserva(LocalDate.now());
        reserva.setFechaFinalizacion(LocalDate.now().plusDays(1));
        reserva.setCosto(100.0);

        when(session.get(Reserva.class, id)).thenReturn(reserva);

        // When
        Reserva result = repoReserva.obtenerReservaPorId(id);

        // Then
        assertNotNull(result);
        assertEquals(reserva.getId(), result.getId());
        assertEquals(reserva.getFechaReserva(), result.getFechaReserva());
        assertEquals(reserva.getFechaFinalizacion(), result.getFechaFinalizacion());
        assertEquals(reserva.getCosto(), result.getCosto(), 0.001);
    }

    @Test
    @Transactional
    @Rollback
    public void testActualizarReserva() {
        // Given
        Reserva reserva = new Reserva();
        reserva.setId(1L);
        reserva.setFechaReserva(LocalDate.of(2024, 6, 1));
        reserva.setFechaFinalizacion(LocalDate.of(2024, 6, 10));

        // When
        repoReserva.actualizarReserva(reserva);

        // Then
        verify(session).update(reserva);
    }

    @Test
    @Transactional
    @Rollback
    public void testEliminarReserva() {
        // Given
        Long id = 1L;
        Reserva reserva = new Reserva();
        reserva.setId(id);

        when(session.load(Reserva.class, id)).thenReturn(reserva);

        // When
        repoReserva.eliminarReserva(id);

        // Then
        verify(session).delete(reserva);
    }

    @Test
    @Transactional
    @Rollback
    public void testTieneReservaActiva() {
        // Given
        Long idLocker = 1L;
        Long idUsuario = 1L;
        LocalDate now = LocalDate.now();
        String hql = "SELECT COUNT(*) FROM Reserva WHERE usuario.id = :idUsuario AND locker.id = :idLocker AND fechaFinalizacion > :hoy";

        Query<Long> query = mock(Query.class); // Mock del Query
        when(session.createQuery(hql, Long.class)).thenReturn(query);
        when(query.setParameter("idUsuario", idUsuario)).thenReturn(query);
        when(query.setParameter("idLocker", idLocker)).thenReturn(query);
        when(query.setParameter("hoy", now)).thenReturn(query);
        when(query.uniqueResult()).thenReturn(1L);

        // When
        boolean tieneReserva = repoReserva.tieneReservaActiva(idUsuario, idLocker);

        // Then
        assertTrue(tieneReserva);
    }


}
