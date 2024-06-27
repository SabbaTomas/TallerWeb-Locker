package com.tallerwebi.dominio.usuario;

import com.tallerwebi.dominio.locker.Locker;

import java.util.List;

public interface ServicioUsuario {

    Usuario buscarUsuarioPorId(Long id);

    List<Locker> obtenerLockersPorCodigoPostalUsuario(String codigoPostal);

    List<Locker> obtenerTodosLosLockersRegistrados(Long idUsuario);

    void guardarUsuario(Usuario usuario);

    void actualizarDatosUsuario(Long id, Usuario usuarioActualizado);
}
