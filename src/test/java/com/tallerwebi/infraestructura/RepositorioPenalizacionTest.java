package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.penalizacion.Penalizacion;
import com.tallerwebi.dominio.penalizacion.RepositorioPenalizacion;
import com.tallerwebi.infraestructura.config.HibernateTestInfraestructuraConfig;
import com.tallerwebi.infraestructura.penalizacion.RepositorioPenalizacionImpl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@Transactional
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {HibernateTestInfraestructuraConfig.class})
public class RepositorioPenalizacionTest {

    @InjectMocks
    private RepositorioPenalizacionImpl repositorioPenalizacion;

    @Mock
    private SessionFactory sessionFactory;

    @Mock
    private Session session;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(sessionFactory.getCurrentSession()).thenReturn(session);
    }

    @Test
    @Rollback
    public void dadoQuePenalizacionEsGuardadaEntoncesDebeSerPersistida() {
        Penalizacion penalizacion = new Penalizacion();
        repositorioPenalizacion.guardarPenalizacion(penalizacion);
        Mockito.verify(session).save(penalizacion);
    }

    @Test
    @Rollback
    public void dadoQuePenalizacionEsObtenidaPorIdEntoncesDebeSerRecuperada() {
        Penalizacion penalizacion = new Penalizacion();
        Mockito.when(session.get(Penalizacion.class, 1L)).thenReturn(penalizacion);
        Penalizacion resultado = repositorioPenalizacion.obtenerPenalizacionPorId(1L);
        assertEquals(penalizacion, resultado);
    }

    @Test
    @Rollback
    public void dadoQueObtenerPenalizacionesPorReservaIdEntoncesDebeDevolverListaDePenalizaciones() {
        // Crear las penalizaciones de prueba
        Penalizacion penalizacion1 = new Penalizacion();
        Penalizacion penalizacion2 = new Penalizacion();
        List<Penalizacion> penalizaciones = Arrays.asList(penalizacion1, penalizacion2);

        // Crear el mock del Query
        Query<Penalizacion> query = Mockito.mock(Query.class);

        // Configurar los mocks
        when(session.createQuery(anyString(), eq(Penalizacion.class))).thenReturn(query);
        when(query.setParameter("reservaId", 1L)).thenReturn(query);
        when(query.list()).thenReturn(penalizaciones);

        // Llamar al método a probar
        List<Penalizacion> result = repositorioPenalizacion.obtenerPenalizacionesPorReservaId(1L);

        // Verificar el resultado
        assertEquals(penalizaciones, result);
    }


    @Test
    @Rollback
    public void dadoQueObtenerPenalizacionesPorUsuarioEntoncesDebeDevolverListaDePenalizaciones() {
        List<Penalizacion> penalizaciones = Arrays.asList(new Penalizacion(), new Penalizacion());
        Query<Penalizacion> query = Mockito.mock(Query.class);
        Mockito.when(session.createQuery(anyString(), eq(Penalizacion.class))).thenReturn(query);
        Mockito.when(query.setParameter("usuarioId", 1L)).thenReturn(query);
        Mockito.when(query.list()).thenReturn(penalizaciones);

        List<Penalizacion> result = repositorioPenalizacion.obtenerPenalizacionesPorUsuario(1L);
        assertEquals(penalizaciones, result);
    }

    @Test
    @Rollback
    public void dadoQueObtenerMontosPorUsuarioEntoncesDebeDevolverListaDeMontos() {
        List<Object[]> montos = Arrays.asList(
                new Object[]{1L, 100.0, 50.0},
                new Object[]{2L, 200.0, 75.0}
        );
        Query query = Mockito.mock(Query.class);
        Mockito.when(session.createQuery(anyString(), eq(Object[].class))).thenReturn(query);
        Mockito.when(query.setParameter("usuarioId", 1L)).thenReturn(query);
        Mockito.when(query.getResultList()).thenReturn(montos);

        List<Object[]> result = repositorioPenalizacion.obtenerMontosPorUsuario(1L);
        assertEquals(montos, result);
    }
}
