package com.tallerwebi.infraestructura;

import com.tallerwebi.dominio.locker.Enum.TipoLocker;
import com.tallerwebi.dominio.locker.Locker;
import com.tallerwebi.dominio.locker.RepositorioDatosLocker;
import com.tallerwebi.dominio.locker.ServicioLocker;
import com.tallerwebi.dominio.locker.ServicioLockerImpl;
import com.tallerwebi.infraestructura.config.HibernateTestInfraestructuraConfig;
import com.tallerwebi.infraestructura.locker.RepositorioDatosLockerImpl;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@Transactional
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
        this.servicioLocker = new ServicioLockerImpl(this.repolocker);
    }

    @Test
    @Rollback
    public void dadoQueSeGuardaLockerSePuedeObtenerPorId() {
        // Preparación
        Locker locker1 = new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "1704");

        // Ejecución
        this.sessionFactory.getCurrentSession().save(locker1);

        // Verificación
        Locker lockerObtenido1 = repolocker.obtenerLockerPorId(locker1.getId());

        assertThat(lockerObtenido1, equalTo(locker1));
    }

    @Test
    @Rollback
    public void dadoQueSeEliminaLockerNoExistenteNoSeLanzaExcepcion() {
        // Ejecución y verificación
        assertDoesNotThrow(() -> repolocker.eliminar(9999L));
    }

    @Test
    @Rollback
    public void dadoQueSeActualizaLockerCambiaSuTipo() {
        // Preparación
        TipoLocker tipoLockerInicial = TipoLocker.PEQUENIO;
        TipoLocker tipoLockerNuevo = TipoLocker.MEDIANO;
        String codigoPostal = "1704";
        Locker nuevoLocker = new Locker(tipoLockerInicial, 40.7128, -74.0060, codigoPostal);
        this.sessionFactory.getCurrentSession().save(nuevoLocker);

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
    public void dadoQueNoHayLockersAlEliminarTodosNoSeLanzaExcepcion() {
        // Ejecución y verificación
        assertDoesNotThrow(() -> repolocker.eliminarTodos());
    }

    @Test
    @Rollback
    public void dadoQueSeGuardanVariosLockersSeObtienenLosSeleccionados() {
        // Preparación
        Locker locker1 = new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "1704");
        Locker locker2 = new Locker(TipoLocker.MEDIANO, 40.7129, -74.0061, "1754");
        locker1.setSeleccionado(true);
        locker2.setSeleccionado(true);
        this.sessionFactory.getCurrentSession().save(locker1);
        this.sessionFactory.getCurrentSession().save(locker2);

        // Ejecución
        List<Locker> lockersSeleccionados = repolocker.obtenerSeleccionados();

        // Verificación
        assertTrue(lockersSeleccionados.contains(locker1));
        assertTrue(lockersSeleccionados.contains(locker2));
    }

    @Test
    @Rollback
    public void dadoQueSeGuardanLockersYSeEliminaUnoSoloSeEliminaElLockerCorrecto() {
        // Preparación
        Locker locker1 = new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "1704");
        Locker locker2 = new Locker(TipoLocker.MEDIANO, 40.7129, -74.0061, "1754");
        this.sessionFactory.getCurrentSession().save(locker1);
        this.sessionFactory.getCurrentSession().save(locker2);

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
    public void dadoQueSeGuardaLockerConLatitudYLongitudMaximasSePuedeObtener() {
        // Preparación
        Locker locker = new Locker(TipoLocker.PEQUENIO, 90.0, 180.0, "9999");
        this.sessionFactory.getCurrentSession().save(locker);

        // Ejecución
        Locker lockerObtenido = repolocker.obtenerLockerPorId(locker.getId());

        // Verificación
        assertThat(lockerObtenido, equalTo(locker));
    }

    @Test
    @Rollback
    public void dadoQueSeGuardaLockerConLatitudYLongitudMinimasSePuedeObtener() {
        // Preparación
        Locker locker = new Locker(TipoLocker.PEQUENIO, -90.0, -180.0, "0000");
        this.sessionFactory.getCurrentSession().save(locker);

        // Ejecución
        Locker lockerObtenido = repolocker.obtenerLockerPorId(locker.getId());

        // Verificación
        assertThat(lockerObtenido, equalTo(locker));
    }

    @Test
    @Rollback
    public void dadoQueSeGuardaLockerConLatitudYLongitudCeroSePuedeObtener() {
        // Preparación
        Locker locker = new Locker(TipoLocker.PEQUENIO, 0.0, 0.0, "0000");
        this.sessionFactory.getCurrentSession().save(locker);

        // Ejecución
        Locker lockerObtenido = repolocker.obtenerLockerPorId(locker.getId());

        // Verificación
        assertThat(lockerObtenido, equalTo(locker));
    }

    @Test
    @Rollback
    public void dadoQueSeGuardaLockerConCodigoPostalLargoSePuedeObtener() {
        // Preparación
        Locker locker = new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "1234567890");
        this.sessionFactory.getCurrentSession().save(locker);

        // Ejecución
        Locker lockerObtenido = repolocker.obtenerLockerPorId(locker.getId());

        // Verificación
        assertThat(lockerObtenido, equalTo(locker));
    }

    @Test
    @Rollback
    public void dadoQueSeActualizaLockerConCodigoPostalLargoCambiaSuCodigoPostal() {
        // Preparación
        Locker locker = new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "1704");
        this.sessionFactory.getCurrentSession().save(locker);
        locker.setCodigo_postal("1234567890");

        // Ejecución
        repolocker.actualizar(locker.getId(), locker.getTipo());

        // Verificación
        Locker lockerActualizado = repolocker.obtenerLockerPorId(locker.getId());
        assertThat(lockerActualizado.getCodigo_postal(), equalTo("1234567890"));
    }

    @Test
    @Rollback
    public void dadoQueSeGuardaLockerSinSeleccionarNoSeIncluyeEnLockersSeleccionados() {
        // Preparación
        Locker locker = new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "1704");
        locker.setSeleccionado(false);
        this.sessionFactory.getCurrentSession().save(locker);

        // Ejecución
        List<Locker> lockersSeleccionados = repolocker.obtenerSeleccionados();

        // Verificación
        assertFalse(lockersSeleccionados.contains(locker));
    }

    @Test
    @Rollback
    public void dadoQueSeGuardaLockerConCodigoPostalVacioSePuedeObtener() {
        // Preparación
        Locker locker = new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "");
        this.sessionFactory.getCurrentSession().save(locker);

        // Ejecución
        Locker lockerObtenido = repolocker.obtenerLockerPorId(locker.getId());

        // Verificación
        assertThat(lockerObtenido, equalTo(locker));
    }

    @Test
    @Rollback
    public void dadoQueSeObtienenLockersConCoordenadasExtremasSeObtienenCorrectamente() {
        // Preparación
        Locker locker1 = new Locker(TipoLocker.PEQUENIO, 90.0, 180.0, "9999");
        Locker locker2 = new Locker(TipoLocker.MEDIANO, -90.0, -180.0, "0000");
        this.sessionFactory.getCurrentSession().save(locker1);
        this.sessionFactory.getCurrentSession().save(locker2);

        // Ejecución
        List<Locker> lockersPorCoordenadas = repolocker.obtenerLockersPorRangoDeCoordenadas(-90.0, 90.0, -180.0, 180.0);

        // Verificación
        assertTrue(lockersPorCoordenadas.contains(locker1));
        assertTrue(lockersPorCoordenadas.contains(locker2));
    }

    @Test
    @Rollback
    public void dadoQueSeEncuentranLockersPorCercaniaSeObtienenCorrectamente() {
        // Preparación
        Locker locker1 = new Locker(TipoLocker.PEQUENIO, 40.7128, -74.0060, "1704");
        Locker locker2 = new Locker(TipoLocker.MEDIANO, 40.7129, -74.0061, "1754");
        this.sessionFactory.getCurrentSession().save(locker1);
        this.sessionFactory.getCurrentSession().save(locker2);

        // Ejecución
        List<Locker> lockersCercanos = repolocker.encontrarLockersPorCercania(40.7128, -74.0060, 1.0);

        // Verificación
        assertTrue(lockersCercanos.contains(locker1));
        assertTrue(lockersCercanos.contains(locker2));
    }

}
