package com.saborhimalaya.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false, unique = true) // Se pone unique = true para que no haya dos usuarios con el mismo login
    private String login;

    @Column(nullable = false, unique = true) // Se pone unique = true para que no haya dos usuarios con el mismo email
    private String email;

    @Column(nullable = false)
    private String contrasena;

    @Column(nullable = false)
    private boolean activo;

    protected Usuario() {
    }

    public Usuario(String nombre, String direccion, String login, String email,
            String contrasena, boolean activo) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.login = login;
        this.email = email;
        this.contrasena = contrasena;
        this.activo = activo;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
