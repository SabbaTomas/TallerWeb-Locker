package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Locker;
import com.tallerwebi.dominio.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.reserva.ServicioReserva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/reserva")
public class ControladorReserva {

    @Autowired
    private ServicioReserva servicioReserva;

    @GetMapping
    public String mostrarFormularioReserva(Model model) {
        model.addAttribute("reservaDatos", new ReservaDatos());
        return "formularioReserva";
    }

    @PostMapping
    public String registrarReserva(@ModelAttribute ReservaDatos reservaDatos, Model model) {
        try {
            Usuario usuario = servicioReserva.consultarUsuarioDeReserva(reservaDatos.getEmail(), reservaDatos.getPassword());
            if (usuario != null) {
                Locker locker = new Locker();
                locker.setId(reservaDatos.getIdLocker());
                servicioReserva.registrarReserva(usuario, locker);
                model.addAttribute("mensaje", "Reserva registrada exitosamente");
            } else {
                model.addAttribute("mensaje", "Usuario no encontrado o credenciales incorrectas");
            }
        } catch (UsuarioExistente e) {
            model.addAttribute("mensaje", "El usuario ya tiene una reserva activa");
        } catch (Exception e) {
            model.addAttribute("mensaje", "Ocurrió un error al registrar la reserva");
        }
        return "resultadoReserva";
    }
}
