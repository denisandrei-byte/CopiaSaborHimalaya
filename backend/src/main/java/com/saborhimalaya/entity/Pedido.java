package com.saborhimalaya.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "pedido")
@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class Pedido {

    public static final String EN_PREPARACION = "En preparación";
    public static final String RECIBIDO = "Recibido";
    public static final String ENTREGADO = "Entregado";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    @NonNull
    private Usuario usuario;

    @Column(nullable = false, precision = 10, scale = 2)
    @NonNull
    private BigDecimal importeTotal;

    @Column(nullable = false)
    @NonNull
    private String estado;

    @Column(nullable = false)
    @NonNull
    private LocalDateTime fechaHoraCreacion;

}
