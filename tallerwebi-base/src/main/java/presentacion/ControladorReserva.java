package presentacion;

import com.fasterxml.jackson.databind.ObjectMapper;
import dominio.Locker;
import dominio.Usuario;
import dominio.excepcion.ReservaActivaExistenteException;
import dominio.excepcion.UsuarioExistente;
import dominio.locker.ServicioLocker;
import dominio.reserva.Reserva;
import dominio.reserva.ServicioReserva;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@Controller
@RequestMapping("/reserva")
public class ControladorReserva {

    @Autowired
    private ServicioReserva servicioReserva;

    @Autowired
    private ServicioLocker servicioLocker;

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(ControladorReserva.class);

    @Transactional
    @GetMapping("/formulario")
    public String mostrarFormularioReserva(@RequestParam("idUsuario") Long idUsuario, @RequestParam("idLocker") Long idLocker, Model model) {
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
    @Transactional
    @PostMapping("/registrar")
    public String registrarReserva(@RequestParam("fechaReserva") String fechaReserva,
                                   @RequestParam("fechaFinalizacion") String fechaFinalizacion,
                                   @RequestParam("locker.id") Long idLocker,
                                   @RequestParam("usuario.id") Long idUsuario,
                                   @RequestParam("usuario.email") String email,
                                   @RequestParam("usuario.password") String password,
                                   Model model) {
        try {
            LocalDate fechaInicio = LocalDate.parse(fechaReserva);
            LocalDate fechaFin = LocalDate.parse(fechaFinalizacion);

            Usuario usuario = new Usuario();
            usuario.setId(idUsuario);
            usuario.setEmail(email);
            usuario.setPassword(password);
            Locker locker = servicioLocker.obtenerLockerPorId(idLocker);

            Reserva reserva = new Reserva();
            reserva.setFechaReserva(fechaInicio);
            reserva.setFechaFinalizacion(fechaFin);
            reserva.setLocker(locker);
            reserva.setUsuario(usuario);

            Reserva nuevaReserva = servicioReserva.registrarReserva(reserva);

            model.addAttribute("mensaje", "Reserva registrada exitosamente");
            model.addAttribute("reservaId", nuevaReserva.getId());
            model.addAttribute("lockerId", idLocker);
            model.addAttribute("costoTotal", nuevaReserva.getCosto());
            model.addAttribute("usuarioId", idUsuario);
            model.addAttribute("cantidadLockers", 1);
            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("fechaFin", fechaFin);

            try {
                String jsonReserva = objectMapper.writeValueAsString(nuevaReserva);
                model.addAttribute("jsonReserva", jsonReserva);
            } catch (Exception e) {
                logger.error("Error al serializar la reserva a JSON", e);
                model.addAttribute("mensaje", "Error al serializar la reserva a JSON: " + e.getMessage());
            }

            return "resultadoReserva";
        } catch (ReservaActivaExistenteException e) {
            model.addAttribute("mensaje", "El usuario ya tiene una reserva activa para este locker.");
            logger.warn("Reserva no registrada: {}", e.getMessage());
        } catch (UsuarioExistente| DateTimeParseException e) {
            model.addAttribute("mensaje", e.getMessage());
            logger.warn("Reserva no registrada: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Ocurrió un error al registrar la reserva", e);
            model.addAttribute("mensaje", "Ocurrió un error al registrar la reserva. Error: " + e.getMessage());
        }
        model.addAttribute("lockerId", idLocker);
        model.addAttribute("usuarioId", idUsuario);
        return "resultadoReserva";
    }



}
