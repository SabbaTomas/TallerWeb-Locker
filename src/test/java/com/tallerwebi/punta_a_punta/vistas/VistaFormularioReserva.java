package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;

public class VistaFormularioReserva extends VistaWeb {

    public VistaFormularioReserva(Page page) {
        super(page);
        page.navigate("http://localhost:8080/lockers/reserva/formulario?idUsuario=1&idLocker=3");
    }

    public String obtenerTextoDeLabelFechaInicio() {
        return this.obtenerTextoDelElemento("label[for='fechaInicio']");
    }

    public String obtenerTextoDeLabelFechaFin() {
        return this.obtenerTextoDelElemento("label[for='fechaFin']");
    }

    public void darClickEnReserva() {
        this.darClickEnElElemento("button[type='submit']");
    }
}
