package com.tallerwebi.infraestructura.usuario;

import com.tallerwebi.dominio.locker.Locker;
import com.tallerwebi.dominio.usuario.RepositorioUsuario;
import com.tallerwebi.dominio.usuario.ServicioUsuario;
import com.tallerwebi.dominio.usuario.Usuario;
import com.tallerwebi.dominio.excepcion.ParametrosDelLockerInvalidos;
import com.tallerwebi.dominio.excepcion.PasswordInvalido;
import com.tallerwebi.dominio.excepcion.UsuarioNoEncontrado;
import com.tallerwebi.dominio.locker.ServicioLocker;
import com.tallerwebi.dominio.reserva.RepositorioReserva;
import com.tallerwebi.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioUsuarioImpl implements ServicioUsuario {

    private final RepositorioUsuario repositorioUsuario;
    private final ServicioLocker servicioLocker;
    private final RepositorioReserva repositorioReserva;

    @Autowired
    public ServicioUsuarioImpl(RepositorioUsuario repositorioUsuario, ServicioLocker servicioLocker, RepositorioReserva repositorioReserva) {
        this.repositorioUsuario = repositorioUsuario;
        this.servicioLocker = servicioLocker;
        this.repositorioReserva = repositorioReserva;
    }

    @Override
    public Usuario buscarUsuarioPorId(Long id) {
        return repositorioUsuario.buscarUsuarioPorId(id);
    }

    @Override
    public List<Locker> obtenerLockersPorCodigoPostalUsuario(String codigoPostal) {
        if (codigoPostal == null || codigoPostal.isEmpty()) {
            throw new IllegalArgumentException("El código postal no puede ser nulo o vacío.");
        }

        Usuario usuario = repositorioUsuario.buscarUsuarioPorCodigoPostal(codigoPostal);
        if (usuario == null) {
            throw new UsuarioNoEncontrado("No se encontró ningún usuario con el código postal: " + codigoPostal);
        }

        List<Locker> lockers = servicioLocker.obtenerLockersPorCodigoPostal(codigoPostal);
        if (lockers.isEmpty()) {
            throw new ParametrosDelLockerInvalidos("No se encontraron lockers disponibles para el código postal: " + codigoPostal);
        }

        return lockers;
    }

    @Override
    public List<Locker> obtenerTodosLosLockersRegistrados(Long idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("El ID del usuario no puede ser nulo.");
        }

        return repositorioReserva.obtenerLockersPorIdUsuario(idUsuario);
    }

    @Override
    public void guardarUsuario(Usuario usuario) {
        repositorioUsuario.guardar(usuario);
    }

    @Override
    public void actualizarDatosUsuario(Long idUsuario, Usuario usuarioActualizado) {
        Usuario usuarioExistente = repositorioUsuario.buscarUsuarioPorId(idUsuario);
        if (usuarioExistente == null) {
            throw new UsuarioNoEncontrado("No se encontró ningún usuario con el ID: " + idUsuario);
        }

        String newPasswordPlana = usuarioActualizado.getPassword();
        if (!esPasswordValido(newPasswordPlana)) {
            throw new PasswordInvalido("La contraseña proporcionada no es válida");
        }

        String newHashedPassword = MD5Util.hash(newPasswordPlana);

        usuarioExistente.setEmail(usuarioActualizado.getEmail());
        usuarioExistente.setCodigoPostal(usuarioActualizado.getCodigoPostal());
        usuarioExistente.setPassword(newHashedPassword);

        repositorioUsuario.modificar(usuarioExistente);
    }

    private boolean esPasswordValido(String password) {
        return password != null && password.length() >= 8 && password.matches(".*\\d.*");
    }



}
