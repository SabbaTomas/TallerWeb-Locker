package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaLogin extends VistaWeb {

    public VistaLogin(Page page) {
        super(page);
        page.navigate("http://localhost:8080/lockers/login");
    }

    public String obtenerTextoDeLaBarraDeNavegacion() {
        return this.obtenerTextoDelElemento("a.navbar-brand");
    }

    public String obtenerMensajeDeError() {
        return this.obtenerTextoDelElemento("p.alert.alert-danger");
    }

    public void escribirEMAIL(String email) {
        this.escribirEnElElemento("#email", email);
    }

    public void escribirClave(String clave) {
        this.escribirEnElElemento("#password", clave);
    }

    public void darClickEnIniciarSesion() {
        this.darClickEnElElemento("button.btn-primary");
    }

    public void darClickEnRegistrarme() {
        this.darClickEnElElemento("a#ir-a-registrarme");
    }
}
