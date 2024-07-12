package com.tallerwebi.presentacion;

import com.tallerwebi.dominio.usuario.excepciones.PasswordInvalido;
import com.tallerwebi.dominio.usuario.DatosLogin;
import com.tallerwebi.util.MD5Util;
import com.tallerwebi.dominio.usuario.ServicioLogin;
import com.tallerwebi.dominio.usuario.Usuario;
import com.tallerwebi.dominio.usuario.excepciones.UsuarioExistente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ControladorLogin {

    private final ServicioLogin servicioLogin;

    @Autowired
    public ControladorLogin(ServicioLogin servicioLogin) {
        this.servicioLogin = servicioLogin;
    }

    @RequestMapping("/login")
    public ModelAndView irALogin() {
        ModelMap modelo = new ModelMap();
        modelo.put("datosLogin", new DatosLogin());
        return new ModelAndView("login", modelo);
    }

    @RequestMapping(path = "/validar-login", method = RequestMethod.POST)
    public ModelAndView validarLogin(@ModelAttribute("datosLogin") DatosLogin datosLogin, HttpServletRequest request) {
        ModelMap model = new ModelMap();

        String hashedPassword = MD5Util.hash(datosLogin.getPassword());
        Usuario usuarioBuscado = servicioLogin.consultarUsuario(datosLogin.getEmail(), hashedPassword);

        if (usuarioBuscado != null) {
            request.getSession().setAttribute("USUARIO_ID", usuarioBuscado.getId());
            request.getSession().setAttribute("ROL", usuarioBuscado.getRol());
            return new ModelAndView("redirect:/home");
        } else {
            model.put("error", "Usuario o clave incorrecta");
            return new ModelAndView("login", model);
        }
    }

    @RequestMapping(path = "/registrarme", method = RequestMethod.POST)
    public ModelAndView registrarme(@ModelAttribute("usuario") Usuario usuario) {
        ModelMap model = new ModelMap();
        try {
            validarPasswordYEncriptarUsuario(usuario, model);
            servicioLogin.registrar(usuario);
        } catch (UsuarioExistente e) {
            model.put("error", "El usuario ya existe");
        } catch (PasswordInvalido e) {
            model.put("error", e.getMessage());
        } catch (Exception e) {
            model.put("error", "Error al registrar el nuevo usuario");
        }

        if (model.containsKey("error")) {
            return new ModelAndView("nuevo-usuario", model);
        } else {
            return new ModelAndView("redirect:/login");
        }
    }

    @RequestMapping(path = "/nuevo-usuario", method = RequestMethod.GET)
    public ModelAndView nuevoUsuario() {
        ModelMap model = new ModelMap();
        model.put("usuario", new Usuario());
        return new ModelAndView("nuevo-usuario", model);
    }

    @RequestMapping(path = "/home", method = RequestMethod.GET)
    public ModelAndView irAHome(HttpServletRequest request) {
        if (request.getSession().getAttribute("USUARIO_ID") == null) {
            return new ModelAndView("redirect:/login");
        }
        return new ModelAndView("home");
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ModelAndView inicio() {
        return new ModelAndView("redirect:/login");
    }

    private void validarPasswordYEncriptarUsuario(Usuario usuario, ModelMap model) throws PasswordInvalido, UsuarioExistente {
        if (!esPasswordValido(usuario.getPassword())) {
            throw new PasswordInvalido("La contraseña proporcionada no es válida. Debe tener al menos 8 caracteres y contener al menos un dígito.");
        }

        String hashedPassword = MD5Util.hash(usuario.getPassword());
        usuario.setPassword(hashedPassword);

        if (servicioLogin.consultarUsuarioPorEmail(usuario.getEmail()) != null) {
            throw new UsuarioExistente();
        }
    }

    private boolean esPasswordValido(String password) {
        return password != null && password.length() >= 8 && password.matches(".*\\d.*");
    }
}
