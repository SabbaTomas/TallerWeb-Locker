package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaHome extends VistaWeb{

    public VistaHome(Page page) {
        super(page);
        page.navigate("http://localhost:8080/lockers/home");
    }

    public String obtenerTextoDeLaBarraDeNavegacion(){
        return this.obtenerTextoDelElemento("nav a.navbar-brand");
    }

    public String obtenerTextoBotonIrALockers(){
        return this.obtenerTextoDelElemento("a.btn-primary:nth-of-type(2)");
    }

    public String obtenerTextoBotonParaVolver() {
        return this.obtenerTextoDelElemento("a.btn-secondary");
    }

    public void darClickEnVolver() {
        this.darClickEnElElemento("a.btn-secondary");
    }

    public void darClickEnIrALockers() {
        this.darClickEnElElemento("a.btn-primary:nth-of-type(2)");
    }
}
