package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.locker.Locker;
import com.tallerwebi.dominio.reserva.Reserva;
import com.tallerwebi.dominio.usuario.Usuario;
import com.tallerwebi.infraestructura.config.HibernateTestInfraestructuraConfig;
import com.tallerwebi.infraestructura.reserva.RepositorioReservaImpl;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@Transactional
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
        MockitoAnnotations.openMocks(this);
        when(sessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    @Rollback
    public void dadoQueSeBuscaLockerPorUsuarioSeObtieneElUsuario() {
        // Preparación
        String email = "test@example.com";
        String password = "password";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        usuario.setPassword(password);
        Query query = mock(Query.class);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.uniqueResult()).thenReturn(usuario);

        // Ejecución
        Usuario foundUsuario = repoReserva.buscarLockerPorUsuario(email, password);

        // Verificación
        assertNotNull(foundUsuario);
        assertEquals(email, foundUsuario.getEmail());
        verify(session).createQuery(anyString());
        verify(query, times(2)).setParameter(anyString(), any());
        verify(query).uniqueResult();
    }

    @Test
    @Rollback
    public void dadoQueSeGuardaUsuarioSeVerificaGuardado() {
        // Preparación
        Usuario usuario = new Usuario();

        // Ejecución
        repoReserva.guardarUsuario(usuario);

        // Verificación
        verify(session).save(usuario);
    }

    @Test
    @Rollback
    public void dadoQueSeBuscaLockerPorUsuarioNoExistenteSeObtieneNull() {
        // Preparación
        String email = "noexistente@example.com";
        String password = "nopassword";
        Query query = mock(Query.class);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.uniqueResult()).thenReturn(null);

        // Ejecución
        Usuario foundUsuario = repoReserva.buscarLockerPorUsuario(email, password);

        // Verificación
        assertNull(foundUsuario);
    }

    @Test
    @Rollback
    public void dadoQueSeBuscaUsuarioSeObtieneElUsuario() {
        // Preparación
        String email = "existing@example.com";
        Usuario usuario = new Usuario();
        usuario.setEmail(email);
        Query query = mock(Query.class);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.uniqueResult()).thenReturn(usuario);

        // Ejecución
        Usuario foundUsuario = repoReserva.buscar(email);

        // Verificación
        assertNotNull(foundUsuario);
        assertEquals(email, foundUsuario.getEmail());
    }

    @Test
    @Rollback
    public void dadoQueSeBuscaUsuarioNoExistenteSeObtieneNull() {
        // Preparación
        String email = "noexistente@example.com";
        Query query = mock(Query.class);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.uniqueResult()).thenReturn(null);

        // Ejecución
        Usuario foundUsuario = repoReserva.buscar(email);

        // Verificación
        assertNull(foundUsuario);
    }

    @Test
    @Rollback
    public void dadoQueSeModificaUsuarioExistenteSeVerificaModificacion() {
        // Preparación
        Usuario usuario = new Usuario();
        usuario.setEmail("existing@example.com");
        usuario.setPassword("existingpassword");

        // Ejecución
        repoReserva.modificar(usuario);

        // Verificación
        verify(session).update(usuario);
    }

    @Test
    @Rollback
    public void dadoQueSeGuardaReservaSeVerificaGuardado() {
        // Preparación
        Reserva reserva = new Reserva();
        reserva.setFechaReserva(LocalDate.of(2024, 6, 1));
        reserva.setFechaFinalizacion(LocalDate.of(2024, 6, 10));

        // Ejecución
        repoReserva.guardarReserva(reserva);

        // Verificación
        verify(session).save(reserva);
    }

    @Test
    @Rollback
    public void dadoQueSeObtieneReservaPorIdSeObtieneReservaCorrecta() {
        // Preparación
        Long id = 1L;
        Reserva reserva = new Reserva();
        reserva.setId(id);
        reserva.setFechaReserva(LocalDate.now());
        reserva.setFechaFinalizacion(LocalDate.now().plusDays(1));
        reserva.setCosto(100.0);

        when(session.get(Reserva.class, id)).thenReturn(reserva);

        // Ejecución
        Reserva result = repoReserva.obtenerReservaPorId(id);

        // Verificación
        assertNotNull(result);
        assertEquals(reserva.getId(), result.getId());
        assertEquals(reserva.getFechaReserva(), result.getFechaReserva());
        assertEquals(reserva.getFechaFinalizacion(), result.getFechaFinalizacion());
        assertEquals(reserva.getCosto(), result.getCosto(), 0.001);
    }

    @Test
    @Rollback
    public void dadoQueSeActualizaReservaSeVerificaActualizacion() {
        // Preparación
        Reserva reserva = new Reserva();
        reserva.setId(1L);
        reserva.setFechaReserva(LocalDate.of(2024, 6, 1));
        reserva.setFechaFinalizacion(LocalDate.of(2024, 6, 10));

        // Ejecución
        repoReserva.actualizarReserva(reserva);

        // Verificación
        verify(session).update(reserva);
    }

    @Test
    @Rollback
    public void dadoQueSeEliminaReservaSeVerificaEliminacion() {
        // Preparación
        Long id = 1L;
        Reserva reserva = new Reserva();
        reserva.setId(id);

        when(session.load(Reserva.class, id)).thenReturn(reserva);

        // Ejecución
        repoReserva.eliminarReserva(id);

        // Verificación
        verify(session).delete(reserva);
    }

    @Test
    @Rollback
    public void dadoQueTieneReservaActivaSeVerificaEstadoActivo() {
        // Preparación
        Long idLocker = 1L;
        Long idUsuario = 1L;
        LocalDate now = LocalDate.now();
        String hql = "SELECT COUNT(*) FROM Reserva WHERE usuario.id = :idUsuario AND locker.id = :idLocker AND fechaFinalizacion > :hoy";

        Query<Long> query = mock(Query.class);
        when(session.createQuery(hql, Long.class)).thenReturn(query);
        when(query.setParameter("idUsuario", idUsuario)).thenReturn(query);
        when(query.setParameter("idLocker", idLocker)).thenReturn(query);
        when(query.setParameter("hoy", now)).thenReturn(query);
        when(query.uniqueResult()).thenReturn(1L);

        // Ejecución
        boolean tieneReserva = repoReserva.tieneReservaActiva(idUsuario, idLocker);

        // Verificación
        assertTrue(tieneReserva);
    }

    @Test
    @Rollback
    public void dadoQueSeObtienenLockersPorIdUsuarioSeObtieneListaLockers() {
        // Preparación
        Long idUsuario = 1L;
        Locker locker = new Locker();
        Query<Locker> query = mock(Query.class);
        when(session.createQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.list()).thenReturn(List.of(locker));

        // Ejecución
        List<Locker> lockers = repoReserva.obtenerLockersPorIdUsuario(idUsuario);

        // Verificación
        assertNotNull(lockers);
        assertEquals(1, lockers.size());
        verify(session).createQuery(anyString());
        verify(query).setParameter(anyString(), any());
        verify(query).list();
    }
}
