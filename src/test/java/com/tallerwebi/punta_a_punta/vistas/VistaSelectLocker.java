package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class VistaSelectLocker extends VistaWeb{
    public VistaSelectLocker(Page page) {
        super(page);
        page.navigate("localhost:8080/lockers/mapa?idUsuario=");
    }

    public String obtenerTextoDeLaBarraDeNavegacion() {
        return this.obtenerTextoDelElemento("#titulo");
    }

    public Locator obtenerElementoDeLaPagina(){
        return this.obtenerElemento("#map");
    }

    public void clicEnBuscarLockers() {
        this.darClickEnElElemento("#btn-buscar");
    }

}
