package com.tallerwebi.dominio.usuario;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface RepositorioUsuario {

    Usuario buscarUsuario(String email, String password);
    void guardar(Usuario usuario);
    Usuario buscar(String email);
    void modificar(Usuario usuario);

    Usuario buscarUsuarioPorEmail(String email);

    Usuario buscarUsuarioPorId(Long id);

    Usuario buscarUsuarioPorCodigoPostal(String codigoPostal);

    boolean existeUsuarioPorId(Long id);

    void eliminar(Long id);

    @SuppressWarnings("unchecked")
    List<Usuario> listarUsuarios();

}

