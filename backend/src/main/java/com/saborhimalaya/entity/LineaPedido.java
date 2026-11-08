package com.saborhimalaya.entity;

import java.math.BigDecimal;

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
@Table(name = "linea_pedido")
public class LineaPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idLinea;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pedido", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_plato")
    private Plato plato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_menu")
    private Menu menu;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario",nullable = false, precision = 10, scale = 2)//name para el nombre de la base de datos
    private BigDecimal precioUnitario;

    protected LineaPedido() {
    }

    public LineaPedido(Pedido pedido, Plato plato, Menu menu, Integer cantidad, BigDecimal precioUnitario) {
        this.pedido = pedido;
        this.plato = plato;
        this.menu = menu;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public Long getIdLinea() {
        return idLinea;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public Plato getPlato() {
        return plato;
    }

    public void setPlato(Plato plato) {
        this.plato = plato;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
}
