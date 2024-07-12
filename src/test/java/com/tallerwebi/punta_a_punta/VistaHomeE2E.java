package com.tallerwebi.punta_a_punta;

import com.microsoft.playwright.*;
import com.tallerwebi.punta_a_punta.vistas.VistaHome;
import com.tallerwebi.punta_a_punta.vistas.VistaLogin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

public class vistaHomeE2E {

    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    VistaHome vistaHome;
    VistaLogin vistaLogin;

    @BeforeAll
    static void abrirNavegador() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(50));
    }

    @AfterAll
    static void cerrarNavegador() {
        playwright.close();
    }

    @BeforeEach
    void crearContextoYPagina() {
        context = browser.newContext();
        Page page = context.newPage();

        vistaLogin = new VistaLogin(page);
        vistaLogin.escribirEMAIL("pruebaTest@unlam.edu.ar");
        vistaLogin.escribirClave("prueba1234");
        vistaLogin.darClickEnIniciarSesion();

        vistaHome = new VistaHome(page);
    }

    @AfterEach
    void cerrarContexto() {
        context.close();
    }

    @Test
    void deberiaDecirUnlamEnElNavbar() {
        String texto = vistaHome.obtenerTextoDeLaBarraDeNavegacion();
        assertThat("Taller Web", equalToIgnoringCase(texto));
    }

    @Test
    void deberiahaberUnBotonDeIrALockers() {
        String texto = vistaHome.obtenerTextoBotonIrALockers();
        assertThat("Ir a Lockers", equalToIgnoringCase(texto));
    }

    @Test
    void deberiahaberUnBotonDeVolver() {
        String texto = vistaHome.obtenerTextoBotonParaVolver();
        assertThat("Volver", equalToIgnoringCase(texto));
    }

    @Test
    void deberiaNavegarALockersSiClicEnIrALockers() {
        vistaHome.darClickEnIrALockers();
        String url = vistaHome.obtenerURLActual();
        assertThat(url, containsStringIgnoringCase("http://localhost:8080/lockers/lockersPorUsuario"));
    }

    @Test
    void deberiaNavegarALoginSiClicEnVolver() {
        vistaHome.darClickEnVolver();
        String url = vistaHome.obtenerURLActual();
        assertThat(url, containsStringIgnoringCase("/login"));
    }
}
