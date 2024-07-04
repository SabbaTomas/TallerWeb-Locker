package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class VistaFormularioReserva extends VistaWeb{

    public VistaFormularioReserva(Page page) {
        super(page);
        page.navigate("localhost:8080/lockers/reserva/formulario?idUsuario=1&idLocker=3");
    }

    public String obtenerTextoDeLabelFechaInicio(){
        return this.obtenerTextoDelElemento("#inicioLabel");
    }


    public String obtenerTextoDeLabelFechaFin() {
        return this.obtenerTextoDelElemento("#finLabel");
    }

    public void darClickEnReserva() {
            this.darClickEnElElemento("#btn-reserva");
    }
}
