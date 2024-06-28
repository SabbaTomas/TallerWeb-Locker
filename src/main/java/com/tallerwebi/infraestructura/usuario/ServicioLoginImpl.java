package com.tallerwebi.infraestructura.usuario;

import com.tallerwebi.dominio.usuario.RepositorioUsuario;
import com.tallerwebi.dominio.usuario.ServicioLogin;
import com.tallerwebi.dominio.usuario.Usuario;
import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service("servicioLogin")
@Transactional
public class ServicioLoginImpl implements ServicioLogin {

    private RepositorioUsuario repositorioUsuario;

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

