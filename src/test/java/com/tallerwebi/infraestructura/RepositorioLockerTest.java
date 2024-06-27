package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.locker.Locker;
import com.tallerwebi.infraestructura.locker.RepositorioDatosLockerImpl;
import com.tallerwebi.infraestructura.locker.ServicioLockerImpl;
import com.tallerwebi.dominio.excepcion.LockerNoEncontrado;
import com.tallerwebi.dominio.excepcion.ParametrosDelLockerInvalidos;
import com.tallerwebi.dominio.locker.RepositorioDatosLocker;
import com.tallerwebi.dominio.locker.ServicioLocker;
import com.tallerwebi.dominio.locker.TipoLocker;
import com.tallerwebi.infraestructura.config.HibernateTestInfraestructuraConfig;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {HibernateTestInfraestructuraConfig.class})
public class RepositorioLockerTest {


    @Autowired
    private SessionFactory sessionFactory;

    private RepositorioDatosLocker repolocker;
    private ServicioLocker servicioLocker;

    @BeforeEach
    public void init() {
        this.repolocker = new RepositorioDatosLockerImpl(this.sessionFactory);
        this.servicioLocker = new ServicioLockerImpl((RepositorioDatosLockerImpl) this.repolocker);
    }


    @Test
    @Rollback
    @Transactional
    public void testObtenerLockersPorTipoInexistente() {
        // Ejecución
        List<Locker> lockersPorTipo = repolocker.obtenerLockersPorTipo(TipoLocker.GRANDE);

        // Verificación
        assertTrue(lockersPorTipo.isEmpty());
    }

    @Test
    @Rollback
    @Transactional
    public void testObtenerLockersPorCodigoPostalInexistente() {
        // Ejecución
        List<Locker> lockersPorCodigoPostal = repolocker.obtenerLockersPorCodigoPostal("9999");

        // Verificación
        assertTrue(lockersPorCodigoPostal.isEmpty());
    }

    @Test
    @Rollback
    @Transactional
    public void testGuardarYObtenerLockers() {
        // Preparación
        Locker locker1 = new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "1704");
        Locker locker2 = new Locker(TipoLocker.MEDIANO, 40.7129, -74.0061, "1754");

        // Ejecución
        repolocker.guardar(locker1);
        repolocker.guardar(locker2);

        // Verificación
        Locker lockerObtenido1 = repolocker.obtenerLockerPorId(locker1.getId());
        Locker lockerObtenido2 = repolocker.obtenerLockerPorId(locker2.getId());

        assertThat(lockerObtenido1, equalTo(locker1));
        assertThat(lockerObtenido2, equalTo(locker2));
    }

    @Test
    @Rollback
    @Transactional
    public void testObtenerLockersCercanosSinResultados() {
        // Preparación
        double latMin = 50.0;
        double latMax = 51.0;
        double lonMin = -0.1;
        double lonMax = 0.1;

        // Ejecución
        List<Locker> lockersPorCoordenadas = repolocker.obtenerLockersPorRangoDeCoordenadas(latMin, latMax, lonMin, lonMax);

        // Verificación
        assertTrue(lockersPorCoordenadas.isEmpty());
    }

    @Test
    @Rollback
    @Transactional
    public void testEliminarLockerNoExistente() {
        // Ejecución y verificación
        assertDoesNotThrow(() -> repolocker.eliminar(9999L));
    }


    @Test
    @Rollback
    @Transactional
    public void testActualizarLocker() {
        // Preparación
        TipoLocker tipoLockerInicial = TipoLocker.PEQUENIO;
        TipoLocker tipoLockerNuevo = TipoLocker.MEDIANO;
        String codigoPostal = "1704";
        Locker nuevoLocker = new Locker(tipoLockerInicial, 40.7128, -74.0060, codigoPostal);
        repolocker.guardar(nuevoLocker);

        // Ejecución
        repolocker.actualizar(nuevoLocker.getId(), tipoLockerNuevo);

        // **Refresh lockerActualizado to ensure it has the latest data**
        sessionFactory.getCurrentSession().refresh(nuevoLocker);

        // Verificación
        Locker lockerActualizado = repolocker.obtenerLockerPorId(nuevoLocker.getId());
        assertThat(lockerActualizado.getTipo(), equalTo(tipoLockerNuevo));
    }


    @Test
    @Rollback
    @Transactional
    public void testObtenerSeleccionadosSinResultados() {
        // Ejecución
        List<Locker> lockersSeleccionados = repolocker.obtenerSeleccionados();

        // Verificación
        assertTrue(lockersSeleccionados.isEmpty());
    }

    @Test
    @Rollback
    @Transactional
    public void testEliminarTodosSinLockers() {
        // Ejecución y verificación
        assertDoesNotThrow(() -> repolocker.eliminarTodos());
    }

    @Test
    @Rollback
    @Transactional
    public void testGuardarLockerNulo() {
        // Ejecución y verificación
        assertThrows(Exception.class, () -> repolocker.guardar(null));
    }

    @Test
    @Rollback
    @Transactional
    public void testActualizarLockerConParametrosNulos() {

        // Verificar que se lanza una excepción cuando idLocker es nulo
        assertThrows(LockerNoEncontrado.class, () -> servicioLocker.actualizarLocker(null, TipoLocker.PEQUENIO));

        // Verificar que se lanza una excepción cuando tipoLocker es nulo
        assertThrows(ParametrosDelLockerInvalidos.class, () -> servicioLocker.actualizarLocker(1L, null));
    }


    @Test
    @Rollback
    @Transactional
    public void testGuardarVariosLockersYObtenerSeleccionados() {
        // Preparación
        Locker locker1 = new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "1704");
        Locker locker2 = new Locker(TipoLocker.MEDIANO, 40.7129, -74.0061, "1754");
        locker1.setSeleccionado(true);
        locker2.setSeleccionado(true);
        repolocker.guardar(locker1);
        repolocker.guardar(locker2);

        // Ejecución
        List<Locker> lockersSeleccionados = repolocker.obtenerSeleccionados();

        // Verificación
        assertTrue(lockersSeleccionados.contains(locker1));
        assertTrue(lockersSeleccionados.contains(locker2));
    }


    @Test
    @Rollback
    @Transactional
    public void testGuardarLockersYEliminarUno() {
        // Preparación
        Locker locker1 = new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "1704");
        Locker locker2 = new Locker(TipoLocker.MEDIANO, 40.7129, -74.0061, "1754");
        repolocker.guardar(locker1);
        repolocker.guardar(locker2);

        // Ejecución
        repolocker.eliminar(locker1.getId());

        sessionFactory.getCurrentSession().refresh(locker1);

        // Verificación
        Locker lockerObtenido1 = repolocker.obtenerLockerPorId(locker1.getId());
        Locker lockerObtenido2 = repolocker.obtenerLockerPorId(locker2.getId());

        assertNotNull(lockerObtenido1);
        assertFalse(lockerObtenido1.getSeleccionado());

        assertNotNull(lockerObtenido2);
        assertTrue(lockerObtenido2.getSeleccionado());
    }


    @Test
    @Rollback
    @Transactional
    public void testGuardarYObtenerLockerConLatitudYLongitudMaximas() {
        // Preparación
        Locker locker = new Locker(TipoLocker.PEQUENIO, 90.0, 180.0, "9999");
        repolocker.guardar(locker);

        // Ejecución
        Locker lockerObtenido = repolocker.obtenerLockerPorId(locker.getId());

        // Verificación
        assertThat(lockerObtenido, equalTo(locker));
    }

    @Test
    @Rollback
    @Transactional
    public void testGuardarYObtenerLockerConLatitudYLongitudMinimas() {
        // Preparación
        Locker locker = new Locker(TipoLocker.PEQUENIO, -90.0, -180.0, "0000");
        repolocker.guardar(locker);

        // Ejecución
        Locker lockerObtenido = repolocker.obtenerLockerPorId(locker.getId());

        // Verificación
        assertThat(lockerObtenido, equalTo(locker));
    }

    @Test
    @Rollback
    @Transactional
    public void testGuardarYObtenerLockerConLatitudYLongitudCero() {
        // Preparación
        Locker locker = new Locker(TipoLocker.PEQUENIO, 0.0, 0.0, "0000");
        repolocker.guardar(locker);

        // Ejecución
        Locker lockerObtenido = repolocker.obtenerLockerPorId(locker.getId());

        // Verificación
        assertThat(lockerObtenido, equalTo(locker));
    }

    @Test
    @Rollback
    @Transactional
    public void testGuardarLockerConCodigoPostalLargo() {
        // Preparación
        Locker locker = new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "1234567890");
        repolocker.guardar(locker);

        // Ejecución
        Locker lockerObtenido = repolocker.obtenerLockerPorId(locker.getId());

        // Verificación
        assertThat(lockerObtenido, equalTo(locker));
    }

    @Test
    @Rollback
    @Transactional
    public void testActualizarLockerConCodigoPostalLargo() {
        // Preparación
        Locker locker = new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "1704");
        repolocker.guardar(locker);
        locker.setCodigo_postal("1234567890");

        // Ejecución
        repolocker.actualizar(locker.getId(), locker.getTipo());

        // Verificación
        Locker lockerActualizado = repolocker.obtenerLockerPorId(locker.getId());
        assertThat(lockerActualizado.getCodigo_postal(), equalTo("1234567890"));
    }


    @Test
    @Rollback
    @Transactional
    public void testGuardarLockerSinSeleccionar() {
        // Preparación
        Locker locker = new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "1704");
        locker.setSeleccionado(false); // Asegurarse de que el locker no esté seleccionado
        repolocker.guardar(locker);

        // Ejecución
        List<Locker> lockersSeleccionados = repolocker.obtenerSeleccionados();

        // Verificación
        assertFalse(lockersSeleccionados.contains(locker));
    }


    @Test
    @Rollback
    @Transactional
    public void testGuardarLockerConCodigoPostalVacio() {
        // Preparación
        Locker locker = new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "");
        repolocker.guardar(locker);

        // Ejecución
        Locker lockerObtenido = repolocker.obtenerLockerPorId(locker.getId());

        // Verificación
        assertThat(lockerObtenido, equalTo(locker));
    }

    @Test
    @Rollback
    @Transactional
    public void testObtenerLockersConCoordenadasExtremas() {
        // Preparación
        Locker locker1 = new Locker(TipoLocker.PEQUENIO, 90.0, 180.0, "9999");
        Locker locker2 = new Locker(TipoLocker.MEDIANO, -90.0, -180.0, "0000");
        repolocker.guardar(locker1);
        repolocker.guardar(locker2);

        // Ejecución
        List<Locker> lockersPorCoordenadas = repolocker.obtenerLockersPorRangoDeCoordenadas(-90.0, 90.0, -180.0, 180.0);

        // Verificación
        assertTrue(lockersPorCoordenadas.contains(locker1));
        assertTrue(lockersPorCoordenadas.contains(locker2));
    }

    @Test
    @Rollback
    @Transactional
    public void testEncontrarLockersPorCercania() {
        // Preparación
        Locker locker1 = new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "1704");
        Locker locker2 = new Locker(TipoLocker.MEDIANO, 40.7129, -74.0061, "1754");
        repolocker.guardar(locker1);
        repolocker.guardar(locker2);

        // Ejecución
        List<Locker> lockersCercanos = repolocker.encontrarLockersPorCercania(40.7128, -74.0060, 1.0);

        // Verificación
        assertTrue(lockersCercanos.contains(locker1));
        assertTrue(lockersCercanos.contains(locker2));
    }

}
