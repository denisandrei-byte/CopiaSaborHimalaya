package com.saborhimalaya.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// US12: datos para configurar el menú diario (P13)
public class ConfigurarMenuRequest {

    @NotNull
    private LocalDate fecha;

    // Identificadores de los platos elegidos para cada tipo de opción
    @NotNull
    @Size(min = 3, max = 3)
    private List<@NotNull Long> entrantes;

    @NotNull
    @Size(min = 3, max = 3)
    private List<@NotNull Long> principales;

    @NotNull
    @Size(min = 2, max = 2)
    private List<@NotNull Long> postres;

    public ConfigurarMenuRequest() {
    }

    public ConfigurarMenuRequest(LocalDate fecha, List<Long> entrantes, List<Long> principales, List<Long> postres) {
        this.fecha = fecha;
        this.entrantes = entrantes;
        this.principales = principales;
        this.postres = postres;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public List<Long> getEntrantes() {
        return entrantes;
    }

    public void setEntrantes(List<Long> entrantes) {
        this.entrantes = entrantes;
    }

    public List<Long> getPrincipales() {
        return principales;
    }

    public void setPrincipales(List<Long> principales) {
        this.principales = principales;
    }

    public List<Long> getPostres() {
        return postres;
    }

    public void setPostres(List<Long> postres) {
        this.postres = postres;
    }
}
