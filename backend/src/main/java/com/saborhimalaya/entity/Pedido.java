package com.saborhimalaya.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.security.web.csrf.CsrfToken;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pedido")
@Data
@NoArgsConstructor
public class Pedido {

    public static final String EN_PREPARACION = "En preparación";
    public static final String RECIBIDO = "Recibido";
    public static final String ENTREGADO = "Entregado";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    @NotNull
    private Usuario usuario;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull
    private BigDecimal importeTotal;

    @Column(nullable = false)
    @NotBlank
    private String estado;

    @Column(nullable = false)
    @NotNull
    private LocalDateTime fechaHoraCreacion;

    public Pedido(Usuario usuario, BigDecimal importeTotal, String estado, LocalDateTime fechaHoraCreacion) {
        this.usuario = usuario;
        this.importeTotal = importeTotal;
        this.estado = estado;
        this.fechaHoraCreacion = fechaHoraCreacion;
    }

}
