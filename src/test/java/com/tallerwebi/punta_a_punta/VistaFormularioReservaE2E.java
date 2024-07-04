package com.tallerwebi.punta_a_punta;

import com.microsoft.playwright.*;
import com.tallerwebi.punta_a_punta.vistas.VistaFormularioReserva;
import com.tallerwebi.punta_a_punta.vistas.VistaLogin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

public class VistaFormularioReservaE2E
{
    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    VistaFormularioReserva vistaFormularioReserva;
    VistaLogin vistaLogin;

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

        vistaFormularioReserva = new VistaFormularioReserva(page);
    }

    @AfterEach
    void cerrarContexto() {
        context.close();
    }

    @Test
    void DebeHaberUnLabelConFechaInicio(){
        String texto = vistaFormularioReserva.obtenerTextoDeLabelFechaInicio();
        assertThat("Fecha Inicio:", equalToIgnoringCase(texto));
    }

    @Test
    void DebeHaberUnLabelConFechaFin(){
        String texto = vistaFormularioReserva.obtenerTextoDeLabelFechaFin();
        assertThat("Fecha Fin:", equalToIgnoringCase(texto));
    }

//    @Test
//    void clicEnReservaDeberiaLlevarAConfirmacionDeReserva(){
//        vistaFormularioReserva.darClickEnReserva();
//        String url = vistaFormularioReserva.obtenerURLActual();
//        assertThat(url, containsStringIgnoringCase("/lockers/reserva/registrar"));
//    }
}
