package com.tallerwebi.dominio;

import com.tallerwebi.dominio.locker.TipoLocker;

import javax.persistence.*;

@Entity
public class Locker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoLocker tipo;

    private String descripcion;
    private boolean seleccionado;
    private Double latitud;
    private Double longitud;

    private String codigo_postal;

    // Constructor sin argumentos
    public Locker() {
    }

    // Constructor con tipo, latitud y longitud
    public Locker(TipoLocker tipo, Double latitud, Double longitud, String codigo_postal) {
        this.tipo = tipo;
        this.latitud = latitud;
        this.longitud = longitud;
        this.seleccionado = true;
        this.codigo_postal = codigo_postal;
    }

    // Constructor con tipo
    public Locker(TipoLocker tipo) {
        this.tipo = tipo;
    }

    public Locker(Long idLocker, TipoLocker tipoLockerNuevo) {
        this.id = idLocker;
        this.tipo = tipoLockerNuevo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoLocker getTipo() {
        return tipo;
    }

    public void setTipo(TipoLocker tipo) {
        this.tipo = tipo;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Boolean getSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(Boolean seleccionado) {
        this.seleccionado = seleccionado;
    }

    public String getCodigo_postal() {
        return codigo_postal;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setCodigo_postal(String codigo_postal) {
        this.codigo_postal = codigo_postal;
    }
}