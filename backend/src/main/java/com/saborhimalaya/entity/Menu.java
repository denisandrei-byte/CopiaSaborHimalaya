package com.saborhimalaya.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "menu")
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMenu;

    @Column(nullable = false, unique = true) // unique = true para asegurar solo un menú por día
    private LocalDate fecha;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private boolean configurado;

    protected Menu() {
    }

    public Menu(LocalDate fecha, BigDecimal precio, boolean configurado) {
        this.fecha = fecha;
        this.precio = precio;
        this.configurado = configurado;
    }

    public Long getIdMenu() {
        return idMenu;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public boolean isConfigurado() {
        return configurado;
    }

    public void setConfigurado(boolean configurado) {
        this.configurado = configurado;
    }
}
