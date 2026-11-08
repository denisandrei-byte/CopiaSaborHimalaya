package com.saborhimalaya.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.saborhimalaya.entity.Categoria;

public class CrearPlatoRequest {

    @NotBlank
    @Size(max = 150)
    private String nombre;

    @NotBlank
    @Size(max = 255)
    private String descripcion;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 8, fraction = 2)
    private BigDecimal precio;

    @NotNull
    private Categoria categoria;

    public CrearPlatoRequest() {
    }

    public CrearPlatoRequest(String nombre, String descripcion, BigDecimal precio, Categoria categoria) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
}
