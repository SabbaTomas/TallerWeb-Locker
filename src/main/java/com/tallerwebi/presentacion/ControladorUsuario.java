package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.locker.Locker;
import com.tallerwebi.dominio.reserva.Reserva;
import com.tallerwebi.dominio.usuario.ServicioUsuario;
import com.tallerwebi.dominio.usuario.excepciones.UsuarioNoEncontrado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ControladorUsuario {

    private final ServicioUsuario servicioUsuario;
    private final HttpSession httpSession;

    @Autowired
    public ControladorUsuario(ServicioUsuario servicioUsuario, HttpSession httpSession) {
        this.servicioUsuario = servicioUsuario;
        this.httpSession = httpSession;
    }

    @GetMapping("/verLockersRegistrados")
    public ModelAndView verLockersRegistrados() {
        ModelAndView mav = new ModelAndView();
        try {
            Long idUsuario = (Long) httpSession.getAttribute("userId");
            List<Locker> lockers = servicioUsuario.obtenerTodosLosLockersRegistrados(idUsuario);

            mav.setViewName("todosLosLockers");
            mav.addObject("lockers", lockers);
        } catch (IllegalArgumentException | UsuarioNoEncontrado e) {
            mav.setViewName("error");
            mav.addObject("mensaje", e.getMessage());
        }
        return mav;
    }
}
