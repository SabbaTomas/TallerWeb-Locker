package com.tallerwebi.presentacion;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserBuyer {
    @Id
    private int id;

    private String title;

    private int quantity;

    private double unit_price;


    private double penalizacion;

    public UserBuyer() {}

    public String getTitle() { return this.title; }

    public int getQuantity() { return this.quantity; }

    public double getUnit_price() { return this.unit_price; }

    public double getPenalizacion() { return this.penalizacion; }

    public void setTitle(String title) { this.title = title; }

    public void setId(int id) { this.id = id; }

    public int getId() { return id; }

    public void setUnit_price(double unit_price) { this.unit_price = unit_price; }

    public void setPenalizacion(double penalizacion) { this.penalizacion = penalizacion; }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
