package com.tallerwebi.dominio.penalizacion;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.tallerwebi.dominio.reserva.Reserva;

import javax.persistence.*;

@Entity
@Table(name = "penalizacion")
public class Penalizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reserva_id", nullable = false)
    @JsonBackReference
    private Reserva reserva;

    @Column(nullable = false)
    private Double monto;

    @Column(nullable = false) // O nullable = true si puede ser opcional
    private String descripcion;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
