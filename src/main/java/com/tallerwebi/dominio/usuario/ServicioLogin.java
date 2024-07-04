package com.tallerwebi.dominio.usuario;

import com.tallerwebi.dominio.usuario.excepciones.UsuarioExistente;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ServicioLogin {

    Usuario consultarUsuario(String email, String password);
    void registrar(Usuario usuario) throws UsuarioExistente;

    Usuario consultarUsuarioPorEmail(String email);

}
