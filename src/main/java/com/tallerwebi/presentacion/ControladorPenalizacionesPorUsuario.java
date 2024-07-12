package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.locker.Locker;
import com.tallerwebi.dominio.penalizacion.Penalizacion;
import com.tallerwebi.dominio.penalizacion.PenalizacionService;
import com.tallerwebi.dominio.reserva.Reserva;
import com.tallerwebi.dominio.reserva.ServicioReserva;
import com.tallerwebi.dominio.usuario.ServicioUsuario;
import com.tallerwebi.dominio.usuario.Usuario;
import com.tallerwebi.dominio.usuario.excepciones.UsuarioNoEncontrado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ControladorPenalizacionesPorUsuario {

    private final ServicioUsuario userService;
    private final ServicioReserva reservaService;
    private final PenalizacionService penalizacionService;
    private final HttpSession httpSession;

    @Autowired
    public ControladorPenalizacionesPorUsuario(ServicioUsuario userService, ServicioReserva reservaService,
                                               PenalizacionService penalizacionService, HttpSession httpSession) {
        this.userService = userService;
        this.reservaService = reservaService;
        this.penalizacionService = penalizacionService;
        this.httpSession = httpSession;
    }

    @GetMapping("/lockersPorUsuario")
    public ModelAndView buscarLockersPorUsuario() {
        ModelAndView mav = new ModelAndView();
        try {
            Long idUsuario = (Long) httpSession.getAttribute("USUARIO_ID");

            if (idUsuario == null) {
                throw new IllegalArgumentException("ID de usuario no encontrado en la sesión");
            }

            reservaService.verificarYAplicarPenalizaciones();

            Usuario usuario = userService.buscarUsuarioPorId(idUsuario);
            List<Reserva> reservas = reservaService.obtenerReservasPorUsuario(idUsuario);
            List<Locker> lockers = reservas.stream()
                    .map(Reserva::getLocker)
                    .distinct()
                    .sorted(Comparator.comparing(Locker::getDescripcion))
                    .collect(Collectors.toList());
            List<Penalizacion> penalizaciones = penalizacionService.obtenerPenalizacionesPorUsuario(idUsuario);

            // Obtener montos por usuario
            List<Object[]> montosPorReserva = penalizacionService.MontosPorUsuario(idUsuario);
            Map<Long, Double> montoTotalPorReserva = penalizacionService.getLongDoubleMap(montosPorReserva);

            // Obtener lista de IDs de reservas sin duplicados
            List<Long> reservaIds = reservas.stream()
                    .map(Reserva::getId).distinct().sorted().collect(Collectors.toList());

            mav.setViewName("lockers-usuario");
            mav.addObject("usuario", usuario);
            mav.addObject("lockers", lockers);
            mav.addObject("reservas", reservas);
            mav.addObject("penalizaciones", penalizaciones);
            mav.addObject("montoTotalPorReserva", montoTotalPorReserva);
            mav.addObject("reservaIds", reservaIds);
        } catch (IllegalArgumentException | UsuarioNoEncontrado e) {
            mav.setViewName("error");
            mav.addObject("mensaje", e.getMessage());
        }
        return mav;
    }
}
