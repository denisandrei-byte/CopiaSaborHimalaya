package com.saborhimalaya.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "eleccion_menu")
public class EleccionMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEleccion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_linea", nullable = false)
    private LineaPedido lineaPedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_plato", nullable = false)
    private Plato plato;

    @Column(nullable = false)
    private String tipo;

    protected EleccionMenu() {
    }

    public EleccionMenu(LineaPedido lineaPedido, Plato plato, String tipo) {
        this.lineaPedido = lineaPedido;
        this.plato = plato;
        this.tipo = tipo;
    }

    public Long getIdEleccion() {
        return idEleccion;
    }

    public LineaPedido getLineaPedido() {
        return lineaPedido;
    }

    public void setLineaPedido(LineaPedido lineaPedido) {
        this.lineaPedido = lineaPedido;
    }

    public Plato getPlato() {
        return plato;
    }

    public void setPlato(Plato plato) {
        this.plato = plato;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
