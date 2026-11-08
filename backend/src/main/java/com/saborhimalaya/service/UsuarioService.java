package com.saborhimalaya.service;

import org.springframework.stereotype.Service;

import com.saborhimalaya.entity.Usuario;
import com.saborhimalaya.repository.UsuarioRepository;

import lombok.AllArgsConstructor;

@Service 
@AllArgsConstructor 
public class UsuarioService {

    private final UsuarioRepository repository;

    public Usuario anadirUsuario(Usuario usuario){
        return repository.save(usuario);
    }
}
