package com.tallerwebi.punta_a_punta;

import com.microsoft.playwright.*;
import com.tallerwebi.punta_a_punta.vistas.VistaHome;
import com.tallerwebi.punta_a_punta.vistas.VistaLogin;
import com.tallerwebi.punta_a_punta.vistas.VistaSelectLocker;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

public class VistaSelectLockerE2E {
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    VistaLogin vistaLogin;
    VistaSelectLocker vistaSelectLocker;

    @BeforeAll
    static void abrirNavegador() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
        //browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(50));

    }   @AfterAll
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

        vistaSelectLocker = new VistaSelectLocker(page);
    }

    @AfterEach
    void cerrarContexto() {
        context.close();
    }

    @Test
    void deberiaDecirSelectALockerEnElTitulo() {
//        String currentUrl = vistaSelectLocker.obtenerURLActual();
//        assertThat(currentUrl, containsStringIgnoringCase("/lockers/mapa?idUsuario="));

        String texto = vistaSelectLocker.obtenerTextoDeLaBarraDeNavegacion();
        assertThat("SELECT A LOCKER", equalToIgnoringCase(texto));
    }

    @Test
    void AlClicEnBuscarDeberiaMostrarElMapa() {
        vistaSelectLocker.clicEnBuscarLockers();

        Locator elemento = vistaSelectLocker.obtenerElementoDeLaPagina();
        assertThat(elemento.isVisible(), equalTo(true));
    }

//    @Test
//    void alHacerClicenReservarDeberiaLlevarAlFormularioDeReserva(){
//        vistaSelectLocker.clicEnBuscarLockers();
//        vistaSelectLocker.clicEnIconoReservar();
//        vistaSelectLocker.clicEnReservar();
//
//        String url = vistaSelectLocker.obtenerURLActual();
//        assertThat(url, containsStringIgnoringCase("ockers/formulario?idUsuario=1&idLocker=3"));
//    }

}
