package com.saborhimalaya.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saborhimalaya.entity.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByLogin(String login); // Para comprobar si el login ya está registrado

    boolean existsByEmail(String email); // Para comprobar si el email ya está registrado

    Optional<Usuario> findByLogin(String login); // Para buscar al usuario por login
}
