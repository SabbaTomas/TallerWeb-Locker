package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.usuario.ServicioUsuario;
import com.tallerwebi.dominio.locker.Locker;
import com.tallerwebi.dominio.usuario.Usuario;
import com.tallerwebi.dominio.reserva.excepciones.ReservaActivaExistenteException;
import com.tallerwebi.dominio.locker.ServicioLocker;
import com.tallerwebi.dominio.reserva.ServicioReserva;
import com.tallerwebi.dominio.usuario.excepciones.UsuarioExistente;
import com.tallerwebi.dominio.reserva.Reserva;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Controller
@RequestMapping("/reserva")
public class ControladorReserva {

    private final ServicioReserva servicioReserva;
    private final ServicioLocker servicioLocker;
    private final ServicioUsuario servicioUsuario;

    @Autowired
    public ControladorReserva(ServicioReserva servicioReserva, ServicioLocker servicioLocker, ServicioUsuario servicioUsuario) {
        this.servicioReserva = servicioReserva;
        this.servicioLocker = servicioLocker;
        this.servicioUsuario = servicioUsuario;
    }

    private Long obtenerIdUsuario(HttpServletRequest request) {
        return (Long) request.getSession().getAttribute("USUARIO_ID");
    }

    private boolean usuarioAutenticado(HttpServletRequest request) {
        return obtenerIdUsuario(request) == null;
    }

    @GetMapping("/formulario")
    public String mostrarFormularioReserva(@RequestParam("idLocker") Long idLocker, Model model, HttpServletRequest request) {
        if (usuarioAutenticado(request)) {
            return "redirect:/login";
        }
        Long idUsuario = obtenerIdUsuario(request);
        try {
            Reserva reserva = servicioReserva.prepararDatosReserva(idUsuario, idLocker);
            model.addAttribute("reserva", reserva);
            model.addAttribute("usuario", reserva.getUsuario());
            model.addAttribute("locker", reserva.getLocker());
            return "formularioReserva";
        } catch (IllegalArgumentException e) {
            model.addAttribute("mensaje", e.getMessage());
            return "error";
        }
    }

    @PostMapping("/registrar")
    public String registrarReserva(@RequestParam("fechaReserva") String fechaReserva,
                                   @RequestParam("fechaFinalizacion") String fechaFinalizacion,
                                   @RequestParam("locker.id") Long idLocker,
                                   HttpServletRequest request,
                                   Model model) {
        if (usuarioAutenticado(request)) {
            return "redirect:/login";
        }
        Long idUsuario = obtenerIdUsuario(request);
        try {
            LocalDate fechaInicio = LocalDate.parse(fechaReserva);
            LocalDate fechaFin = LocalDate.parse(fechaFinalizacion);

            Reserva reserva = crearReserva(idUsuario, idLocker, fechaInicio, fechaFin);

            Reserva nuevaReserva = servicioReserva.registrarReserva(reserva);

            agregarAtributosModelo(model, nuevaReserva, idLocker, idUsuario, fechaInicio, fechaFin);

            return "resultadoReserva";
        } catch (ReservaActivaExistenteException | UsuarioExistente | DateTimeParseException e) {
            model.addAttribute("mensaje", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("mensaje", "Ocurrió un error al registrar la reserva. Error: " + e.getMessage());
        }
        model.addAttribute("lockerId", idLocker);
        model.addAttribute("usuarioId", idUsuario);
        return "resultadoReserva";
    }

    private Reserva crearReserva(Long idUsuario, Long idLocker, LocalDate fechaInicio, LocalDate fechaFin) {
        Usuario usuario = servicioUsuario.buscarUsuarioPorId(idUsuario);
        Locker locker = servicioLocker.obtenerLockerPorId(idLocker);

        Reserva reserva = new Reserva();
        reserva.setFechaReserva(fechaInicio);
        reserva.setFechaFinalizacion(fechaFin);
        reserva.setLocker(locker);
        reserva.setUsuario(usuario);
        reserva.setEstado("pendiente");

        return reserva;
    }

    private void agregarAtributosModelo(Model model, Reserva reserva, Long idLocker, Long idUsuario, LocalDate fechaInicio, LocalDate fechaFin) {
        model.addAttribute("mensaje", "Reserva registrada exitosamente");
        model.addAttribute("reservaId", reserva.getId());
        model.addAttribute("lockerId", idLocker);
        model.addAttribute("costoTotal", reserva.getCosto());
        model.addAttribute("usuarioId", idUsuario);
        model.addAttribute("cantidadLockers", 1);
        model.addAttribute("fechaInicio", fechaInicio);
        model.addAttribute("fechaFin", fechaFin);
    }
}
