package com.tallerwebi.presentacion;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.tallerwebi.dominio.ServicioPago;
import com.tallerwebi.dominio.penalizacion.Penalizacion;
import com.tallerwebi.dominio.penalizacion.PenalizacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
public class ControladorPago {

    @Autowired
    private ServicioPago paymentService;

    @Autowired
    private PenalizacionService penalizacionService;

    @GetMapping(path = "/pagar")
    public ModelAndView irAPago(@RequestParam("reservaId") long reservaId, @RequestParam("costoTotal") double costoTotal) {
        ModelMap modelo = new ModelMap();
        try {
            modelo.put("reservaId", reservaId);
            modelo.put("mensaje", new BigDecimal(costoTotal));
        } catch (Exception e) {
            modelo.put("reservaId", reservaId);
            modelo.put("mensaje", "Error al procesar el costo total: " + e.getMessage());
        }
        return new ModelAndView("pagar", modelo);
    }

    @GetMapping("/pagarPenalizaciones")
    public ModelAndView irAPagoPenalizaciones(
            @RequestParam("reservaSeleccionada") Long reservaId,
            @RequestParam("montoTotal") Optional<Double> montoTotalOpt) {

        ModelMap modelo = new ModelMap();

        try {
            double montoTotal = montoTotalOpt.orElse(0.0);
            List<Penalizacion> penalizaciones = penalizacionService.obtenerPenalizacionesPorReservaId(reservaId);

            double montoPenalizaciones = penalizaciones.stream().mapToDouble(Penalizacion::getMonto).sum();
            montoTotal += montoPenalizaciones;

            modelo.put("reservaId", reservaId);
            modelo.put("cantidadPenalizaciones", penalizaciones.size());
            modelo.put("montoTotal", montoTotal);
            modelo.put("penalizaciones", penalizaciones);
        } catch (Exception e) {
            modelo.put("mensaje", "Error al procesar el monto total: " + e.getMessage());
        }

        return new ModelAndView("pagarPenalizaciones", modelo);
    }



    @RequestMapping(value = "api/mp", method = RequestMethod.POST)
    public String getlist(@RequestBody UserBuyer userBuyer) {
        if (userBuyer == null) {
            return "error jsons";
        }
        String title = userBuyer.getTitle();
        int quantity = userBuyer.getQuantity();
        int price = (int) userBuyer.getUnit_price();
        int id = userBuyer.getId();
        double penalizacion = userBuyer.getPenalizacion();

        try {
            return paymentService.createPreference(title, quantity, price, penalizacion, id);
        } catch (MPException | MPApiException e) {
            return e.toString();
        }
    }

    @RequestMapping(value = "/resultado")
    public ModelAndView success(HttpServletRequest request,
                                @RequestParam("collection_id") String collectionId,
                                @RequestParam("collection_status") String collectionStatus,
                                @RequestParam("external_reference") String externalReference,
                                @RequestParam("payment_type") String paymentType,
                                @RequestParam("merchant_order_id") String merchantOrderId,
                                @RequestParam("preference_id") String preferenceId,
                                @RequestParam("site_id") String siteId,
                                @RequestParam("processing_mode") String processingMode,
                                @RequestParam("merchant_account_id") String merchantAccountId,
                                RedirectAttributes attributes) throws MPException {

        attributes.addFlashAttribute("genericResponse", true);
        attributes.addFlashAttribute("collection_id", collectionId);
        attributes.addFlashAttribute("collection_status", collectionStatus);
        attributes.addFlashAttribute("external_reference", externalReference);
        attributes.addFlashAttribute("payment_type", paymentType);
        attributes.addFlashAttribute("merchant_order_id", merchantOrderId);
        attributes.addFlashAttribute("preference_id", preferenceId);
        attributes.addFlashAttribute("site_id", siteId);
        attributes.addFlashAttribute("processing_mode", processingMode);
        attributes.addFlashAttribute("merchant_account_id", merchantAccountId);

        boolean pagoExitoso = paymentService.verificarPagoConServidor(collectionStatus);
        Long idReserva = Long.valueOf(externalReference);
        ModelMap model = new ModelMap();

        if (pagoExitoso) {
            paymentService.registrarLaReservaComoAprobada(idReserva, "APROBADO");
            model.put("message", "Pago exitoso");
            return new ModelAndView("pago-exitoso", model);
        } else {
            model.put("message", "El pago no se pudo realizar");
            return new ModelAndView("pago-fallido", model);
        }
    }
}
