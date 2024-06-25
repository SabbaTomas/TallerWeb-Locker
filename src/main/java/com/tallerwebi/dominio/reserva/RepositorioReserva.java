package com.tallerwebi.dominio.reserva;

import com.tallerwebi.dominio.Usuario;

public interface RepositorioReserva {

    Usuario buscarLocker(String email, String password);
    void guardar(Usuario usuario);
    Usuario buscar(String email);
    void modificar(Usuario usuario);
    void guardarReserva(Reserva reserva);
}

