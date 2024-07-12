package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class VistaSelectLocker extends VistaWeb {
    public VistaSelectLocker(Page page) {
        super(page);
        page.navigate("http://localhost:8080/lockers/mapa?idUsuario=");
    }

    public String obtenerTextoDeLaBarraDeNavegacion() {
        return this.obtenerTextoDelElemento("h1");
    }

    public Locator obtenerElementoDeLaPagina() {
        return this.obtenerElemento("#map");
    }

    public void clicEnBuscarLockers() {
        this.darClickEnElElemento("#filterForm button[type='submit']");
    }

    public void ingresarCodigoPostal(String codigoPostal) {
        this.page.fill("#codigoPostal", codigoPostal);
    }

    public void esperarElemento(String selector) {
        this.page.waitForSelector(selector, new Page.WaitForSelectorOptions().setTimeout(60000));
    }
}
