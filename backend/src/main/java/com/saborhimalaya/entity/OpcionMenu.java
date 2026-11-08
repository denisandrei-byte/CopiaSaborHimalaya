package com.saborhimalaya.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
// Mismas restricciones que el script de la BD: un plato y una posición por tipo de opción en cada menú
@Table(name = "opcion_menu", uniqueConstraints = {
        @UniqueConstraint(name = "unico_menu_tipo_orden", columnNames = { "id_menu", "tipo_opcion", "orden" }),
        @UniqueConstraint(name = "unico_menu_tipo_plato", columnNames = { "id_menu", "tipo_opcion", "id_plato" }) })
public class OpcionMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOpcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_menu", nullable = false)
    private Menu menu;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_plato", nullable = false)
    private Plato plato;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_opcion", nullable = false, length = 20)
    private TipoOpcion tipoOpcion;

    @Column(nullable = false)
    private Integer orden;

    protected OpcionMenu() {
    }

    public OpcionMenu(Menu menu, Plato plato, TipoOpcion tipoOpcion, Integer orden) {
        this.menu = menu;
        this.plato = plato;
        this.tipoOpcion = tipoOpcion;
        this.orden = orden;
    }

    public Long getIdOpcion() {
        return idOpcion;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public Plato getPlato() {
        return plato;
    }

    public void setPlato(Plato plato) {
        this.plato = plato;
    }

    public TipoOpcion getTipoOpcion() {
        return tipoOpcion;
    }

    public void setTipoOpcion(TipoOpcion tipoOpcion) {
        this.tipoOpcion = tipoOpcion;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }
}
