package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.excepcion.ParametrosDelLockerInvalidos;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.dominio.locker.Locker;
import com.tallerwebi.dominio.usuario.ServicioUsuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ControladorUsuario {

    private final ServicioUsuario servicioUsuario;
    private final HttpSession httpSession;

    @Autowired
    public ControladorUsuario(ServicioUsuario servicioUsuario, HttpSession httpSession) {
        this.servicioUsuario = servicioUsuario;
        this.httpSession = httpSession;
    }

    @GetMapping("/lockersPorUsuario")
    public ModelAndView buscarLockersPorCodigoPostalUsuario() {
        ModelAndView mav = new ModelAndView();
        try {
            String codigoPostal = (String) httpSession.getAttribute("codigoPostalUsuario");
            List<Locker> lockers = servicioUsuario.obtenerLockersPorCodigoPostalUsuario(codigoPostal);

            mav.setViewName("lockers-usuario"); // Usar la nueva vista
            mav.addObject("lockers", lockers);
            mav.addObject("codigoPostal", codigoPostal);
        } catch (IllegalArgumentException | UsuarioNoEncontrado | ParametrosDelLockerInvalidos e) {
            mav.setViewName("error");
            mav.addObject("mensaje", e.getMessage());
        }
        return mav;
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

