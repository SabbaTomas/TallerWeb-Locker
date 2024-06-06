
package com.tallerwebi.dominio;


import com.tallerwebi.dominio.excepcion.UsuarioExistente;
import com.tallerwebi.dominio.locker.RepositorioDatosLocker;
import com.tallerwebi.dominio.locker.RepositorioUsuario;
import com.tallerwebi.dominio.reserva.RepositorioReserva;
import com.tallerwebi.dominio.reserva.Reserva;
import com.tallerwebi.dominio.reserva.ServicioReserva;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioReservaImpl  implements  ServicioReserva

{



    @Autowired
    private RepositorioUsuario repositorioUsuario;

   @Autowired
    private RepositorioDatosLocker repositorioDatosLocker;

    @Autowired
    private RepositorioReserva repositorioReserva;

    @Override
    @Transactional
    public Usuario consultarUsuarioDeReserva(String email, String password) {
        return repositorioUsuario.buscarUsuario(email, password);
    }

    @Override
    @Transactional
    public Usuario consultarLockerDeReserva(Usuario usuario, Locker idLocker) {
        Locker locker = repositorioDatosLocker.obtenerPorId(idLocker.getId());
        if (locker != null) {
            return repositorioUsuario.buscar(usuario.getEmail());
        }
        return null;
    }

    @Override
    @Transactional
    public void registrarReserva(Usuario usuario, Locker idLocker) throws UsuarioExistente {
        Usuario usuarioExistente = repositorioUsuario.buscar(usuario.getEmail());
        if (usuarioExistente != null) {
            throw new UsuarioExistente();
        }

        Locker locker = repositorioDatosLocker.obtenerPorId(idLocker.getId());
        if (locker != null) {
            Reserva reserva = new Reserva();
            reserva.setUsuario(usuario);
            reserva.setLocker(locker);
            reserva.setFechaReserva(new Date(System.currentTimeMillis()));
            // Assuming the end date is set to a day later for demonstration purposes
            reserva.setFechaFinalizacion(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
            repositorioReserva.guardarReserva(reserva);
        }
    }

}
