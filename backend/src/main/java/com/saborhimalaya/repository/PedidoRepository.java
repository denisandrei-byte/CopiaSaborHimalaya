package com.saborhimalaya.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saborhimalaya.entity.Pedido;
import com.saborhimalaya.entity.Usuario;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Optional<List<Pedido>> findAllByUsuario(Usuario usuario);

    Optional<Pedido> findByUsuarioAndIdPedido(Usuario usuario, Long idPedido);
}
