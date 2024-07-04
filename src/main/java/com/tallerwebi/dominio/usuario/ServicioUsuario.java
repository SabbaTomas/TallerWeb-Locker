package com.tallerwebi.dominio.usuario;

import com.tallerwebi.dominio.locker.Locker;
import com.tallerwebi.dominio.reserva.Reserva;
import com.tallerwebi.dominio.usuario.excepciones.UsuarioNoEncontrado;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
public interface ServicioUsuario {

    Usuario buscarUsuarioPorId(Long id);

    List<Locker> obtenerLockersPorCodigoPostalUsuario(String codigoPostal);

    List<Locker> obtenerTodosLosLockersRegistrados(Long idUsuario);

    void guardarUsuario(Usuario usuario);

    void actualizarDatosUsuario(Long id, Usuario usuarioActualizado);

    List<Reserva> obtenerReservasPorUsuario(Long idUsuario) throws UsuarioNoEncontrado;
}
