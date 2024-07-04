package com.tallerwebi.dominio.usuario;

import com.tallerwebi.dominio.usuario.excepciones.UsuarioExistente;
import com.tallerwebi.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service("servicioLogin")
public class ServicioLoginImpl implements ServicioLogin {

    private final RepositorioUsuario repositorioUsuario;

    @Autowired
    public ServicioLoginImpl(RepositorioUsuario repositorioUsuario){
        this.repositorioUsuario = repositorioUsuario;
    }

    @Override
    public Usuario consultarUsuario (String email, String password) {
        String hashedPassword = MD5Util.hash(password);
        return repositorioUsuario.buscarUsuario(email, hashedPassword);
    }

    @Override
    public void registrar(Usuario usuario) throws UsuarioExistente {
        Usuario usuarioEncontrado = repositorioUsuario.buscarUsuarioPorEmail(usuario.getEmail());
        if(usuarioEncontrado != null){
            throw new UsuarioExistente();
        }
        String hashedPassword = MD5Util.hash(usuario.getPassword());
        usuario.setPassword(hashedPassword);
        repositorioUsuario.guardar(usuario);
    }

    @Override
    public Usuario consultarUsuarioPorEmail(String email) {
        return repositorioUsuario.buscarUsuarioPorEmail(email);
    }

}

