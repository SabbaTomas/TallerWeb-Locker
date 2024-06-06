package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.Locker;
import com.tallerwebi.dominio.locker.ServicioLocker;
import com.tallerwebi.dominio.locker.TipoLocker;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/mapa")
public class ControladorLocker {

    final ServicioLocker servicioLocker;

    public ControladorLocker(ServicioLocker servicioLocker) {
        this.servicioLocker = servicioLocker;
    }

    @Transactional
    @GetMapping("/crear-locker")
    public ModelAndView crearLocker(@RequestParam("tipoLocker") TipoLocker tipoLocker) {
        List<Locker> lockers = servicioLocker.obtenerLockersPorTipo(tipoLocker);
        ModelAndView mav = new ModelAndView("crear-locker");
        mav.addObject("lockers", lockers);
        return mav;
    }

    @PostMapping("/actualizar-locker")
    public ModelAndView actualizarLocker(@RequestParam Long idLocker, @RequestParam TipoLocker tipoLocker) {
        servicioLocker.actualizarLocker(idLocker, tipoLocker);
        return new ModelAndView("actualizar-locker");
    }

    @PostMapping("/eliminar-locker")
    public ModelAndView eliminarLocker(@RequestParam Long idLocker) {
        servicioLocker.eliminarLocker(idLocker);
        return new ModelAndView("eliminar-locker");
    }

    @Transactional
    @GetMapping("/lockers-por-tipo")
    public ModelAndView buscarLockersPorTipo(@RequestParam TipoLocker tipoLocker) {
        List<Locker> lockers = servicioLocker.obtenerLockersPorTipo(tipoLocker);
        ModelAndView mav = new ModelAndView("lockers-por-tipo");
        mav.addObject("lockers", lockers);
        return mav;
    }

    @Transactional
    @GetMapping
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

        List<Locker> lockers = servicioLocker.buscarLockers(codigoPostal, latitud, longitud, 5.0);
        boolean mostrarAlternativos = false;

        if (lockers.isEmpty()) {
            lockers = servicioLocker.obtenerLockersSeleccionados();
            mostrarAlternativos = true;
        }

        ModelAndView mav = crearModelAndViewConCentro(lockers, "lockers");
        mav.addObject("codigoPostal", codigoPostal);
        mav.addObject("latitud", latitud);
        mav.addObject("longitud", longitud);
        mav.addObject("mostrarAlternativos", mostrarAlternativos);
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


