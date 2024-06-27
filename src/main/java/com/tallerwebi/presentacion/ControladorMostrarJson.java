package com.tallerwebi.presentacion;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/mostrarJson")
public class ControladorMostrarJson {

    @GetMapping
    public String mostrarJson(@RequestParam("jsonReserva") String jsonReserva, Model model) {
        model.addAttribute("jsonReserva", jsonReserva);
        return "mostrarJson";
    }
}
