package com.tallerwebi.punta_a_punta;

import com.microsoft.playwright.*;
import com.tallerwebi.punta_a_punta.vistas.VistaLogin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
public class VistaLoginE2E {

    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    VistaLogin vistaLogin;

    @BeforeAll
    static void abrirNavegador() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        //browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(50));

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
    }

    @AfterEach
    void cerrarContexto() {
        context.close();
    }

    @Test
    void deberiaDecirLOCKERSEnElNavbar() {
        String texto = vistaLogin.obtenerTextoDeLaBarraDeNavegacion();
        assertThat("LOCKERS", equalToIgnoringCase(texto));
    }

    @Test
    void deberiaDarUnErrorAlNoCompletarElLoginYTocarElBoton() {
        vistaLogin.escribirEMAIL("error@unlam.edu.ar");
        vistaLogin.escribirClave("unlam");
        vistaLogin.darClickEnIniciarSesion();
        String texto = vistaLogin.obtenerMensajeDeError();
        assertThat("Usuario o clave incorrecta", equalToIgnoringCase(texto));
    }

    @Test
    void deberiaNavegarAlHomeSiElUsuarioExiste() {
        vistaLogin.escribirEMAIL("pruebaTest@unlam.edu.ar");
        vistaLogin.escribirClave("prueba1234");
        vistaLogin.darClickEnIniciarSesion();
        String url = vistaLogin.obtenerURLActual();
        assertThat(url, containsStringIgnoringCase("/lockers/home"));
    }

    @Test
    void deberiaNavegarAlRegistrarAlClicEnRegistrarme() {
        vistaLogin.darClickEnRegistrarme();
        String url = vistaLogin.obtenerURLActual();
        assertThat(url, containsStringIgnoringCase("/lockers/nuevo-usuario"));
    }
}
