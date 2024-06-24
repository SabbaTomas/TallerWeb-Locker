package presentacion;

import dominio.Locker;
import dominio.excepcion.ParametrosDelLockerInvalidos;
import dominio.locker.ServicioLocker;
import dominio.locker.TipoLocker;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Controller
public class ControladorLocker {
    private static final Logger logger = LoggerFactory.getLogger(ControladorLocker.class);

    final ServicioLocker servicioLocker;

    public ControladorLocker(ServicioLocker servicioLocker) {
        this.servicioLocker = servicioLocker;
    }

    @Transactional
    @GetMapping("/crear-locker")
    public ModelAndView mostrarFormularioCrearLocker(@RequestParam("tipoLocker") TipoLocker tipoLocker) {
        ModelAndView mav = new ModelAndView("crear-locker");
        Locker nuevoLocker = new Locker();
        nuevoLocker.setTipo(tipoLocker);
        mav.addObject("nuevoLocker", nuevoLocker);
        return mav;
    }

    @Transactional
    @PostMapping("/crear-locker")
    public ModelAndView crearLocker(@ModelAttribute Locker nuevoLocker) {
        ModelAndView mav = new ModelAndView();
        try {
            servicioLocker.crearLocker(nuevoLocker);
            mav.setViewName("mensaje-creacion");
            mav.addObject("message", "¡Locker creado exitosamente!");
            mav.addObject("nuevoLocker", nuevoLocker);
        } catch (Exception e) {
            mav.setViewName("crear-locker");
            mav.addObject("nuevoLocker", nuevoLocker);
            mav.addObject("errorMessage", "Error al crear locker.");
        }
        return mav;
    }
    @GetMapping("/actualizar-locker")
    public String mostrarFormularioActualizar(Model model) {
        return "envio-actualizar-form";
    }

    @Transactional
    @PostMapping("/actualizar-locker")
    public ModelAndView actualizarLocker(@RequestParam Long idLocker, @RequestParam TipoLocker tipoLocker) {
        ModelAndView mav = new ModelAndView();
        try {
            logger.debug("Actualizando locker con id: {} y tipo: {}", idLocker, tipoLocker);
            servicioLocker.actualizarLocker(idLocker, tipoLocker);
            mav.setViewName("envio-actualizar");
        } catch (Exception e) {
            logger.error("Error al actualizar locker: {}", e.getMessage());
            mav.setViewName("error");
            mav.addObject("errorMessage", "Error al actualizar locker: " + e.getMessage());
        }
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception e) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("error");
        mav.addObject("errorMessage", e.getMessage());
        return mav;
    }


    @GetMapping("/eliminar-locker")
    @Transactional
    public String mostrarFormularioEliminar(Model model) {
        return "envio-eliminar-form";
    }

    @PostMapping("/eliminar-locker")
    public ModelAndView eliminarLocker(@RequestParam Long idLocker) {
        ModelAndView mav = new ModelAndView();
        try {
            servicioLocker.eliminarLocker(idLocker);
            mav.setViewName("envio-eliminar");
        } catch (Exception e) {
            logger.error("Error al eliminar: {}", e.getMessage());
            mav.setViewName("error");
            mav.addObject("errorMessage", "Error al eliminar locker.");
        }
        return mav;
    }

    @Transactional
    @GetMapping("/lockers-por-tipo")
    public ModelAndView buscarLockersPorTipo(@RequestParam TipoLocker tipoLocker) {
        ModelAndView mav = new ModelAndView();
        try {
            List<Locker> lockers = servicioLocker.obtenerLockersPorTipo(tipoLocker);
            mav.setViewName("lockers-por-tipo");
            mav.addObject("lockers", lockers);
        } catch (Exception e) {
            mav.setViewName("error");
            mav.addObject("errorMessage", "Error al buscar lockers por tipo.");
        }
        return mav;
    }

    @Transactional
    @GetMapping("/mapa")
    public ModelAndView mostrarLockers() {
        List<Locker> lockers = servicioLocker.obtenerLockersSeleccionados();
        return crearModelAndViewConCentro(lockers, "lockers");
    }


    @Transactional
    @GetMapping("/search")
    public ModelAndView buscarLockersPorCodigoPostal(
            @RequestParam(value = "codigoPostal", required = false) String codigoPostal,
            @RequestParam(value = "latitud", required = false) Double latitud,
            @RequestParam(value = "longitud", required = false) Double longitud) {
        ModelAndView mav = new ModelAndView();
        try {
            List<Locker> lockers = servicioLocker.buscarLockers(codigoPostal, latitud, longitud, 5.0);
            boolean mostrarAlternativos = false;
            String mensajeAlternativos = "No tenemos lockers cerca de ti, pero te ofrecemos estos lockers:";

            if (lockers.isEmpty()) {
                lockers = servicioLocker.obtenerLockersSeleccionados();
                mostrarAlternativos = true;
            }

            mav = crearModelAndViewConCentro(lockers, "lockers");
            mav.addObject("codigoPostal", codigoPostal);
            mav.addObject("latitud", latitud);
            mav.addObject("longitud", longitud);
            mav.addObject("mostrarAlternativos", mostrarAlternativos);
            mav.addObject("mensajeAlternativos", mensajeAlternativos);
        } catch (Exception e) {
            mav.setViewName("error");
            mav.addObject("errorMessage", "Error al buscar lockers.");
        }
        return mav;
    }

    private ModelAndView crearModelAndViewConCentro(List<Locker> lockers, String vista) {
        ModelAndView mav = new ModelAndView(vista);
        mav.addObject("lockers", lockers);

        double[] center;
        int zoom;

        if (!lockers.isEmpty()) {
            double sumLatitud = 0;
            double sumLongitud = 0;
            for (Locker locker : lockers) {
                sumLatitud += locker.getLatitud();
                sumLongitud += locker.getLongitud();
            }
            center = new double[]{sumLatitud / lockers.size(), sumLongitud / lockers.size()};
            zoom = 14;
        } else {
            center = new double[]{-34.6821, -58.5638}; // Centro predeterminado
            zoom = 12;
        }

        mav.addObject("center", center);
        mav.addObject("zoom", zoom);
        return mav;
    }

}


