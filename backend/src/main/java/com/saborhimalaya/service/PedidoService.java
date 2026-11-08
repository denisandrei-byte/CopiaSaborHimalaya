package com.saborhimalaya.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import com.saborhimalaya.dto.MenuPedido;
import com.saborhimalaya.dto.PedidoRequest;
import com.saborhimalaya.dto.PlatoPedido;
import com.saborhimalaya.entity.Pedido;
import com.saborhimalaya.entity.Plato;
import com.saborhimalaya.entity.Usuario;
import com.saborhimalaya.exception.PedidoNoEncontradoException;
import com.saborhimalaya.exception.PedidosNoEncontradosException;
import com.saborhimalaya.exception.PlatoNoEncontradoException;
import com.saborhimalaya.exception.UsuarioNoExisteException;
import com.saborhimalaya.repository.PedidoRepository;
import com.saborhimalaya.repository.PlatoRepository;
import com.saborhimalaya.repository.UsuarioRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class PedidoService {

        private final UsuarioRepository usuarioRepository;
        private final PlatoRepository platoRepository;
        private final LineaPedidoService lineaPedidoService;
        private final PedidoRepository repository;

        @Transactional
        public Pedido AnadirPedido(PedidoRequest request, String login) {
                List<PlatoPedido> platosPedidos = request.getPlatos();
                List<MenuPedido> menuPedidos = request.getMenus();
                Pedido nuevo = new Pedido(
                                usuarioRepository.findByLogin(login)
                                                .orElseThrow(() -> new UsuarioNoExisteException(login)),
                                CalcularImporteRequest(platosPedidos, menuPedidos), Pedido.EN_PREPARACION,
                                LocalDateTime.now());
                Pedido creado = repository.save(nuevo);
                if (platosPedidos != null) {
                        for (PlatoPedido pl : platosPedidos) {
                                lineaPedidoService.AnadirLineaPedidoPlato(pl, creado);
                        }
                }
                if (menuPedidos != null) {
                        for (MenuPedido men : menuPedidos) {
                                lineaPedidoService.AnadirLineaPedidoMenu(men, creado);
                        }
                }
                return creado;
        }

        public List<Pedido> ObtenerPedidos(String login) {
                Usuario usuario = usuarioRepository.findByLogin(login)
                                .orElseThrow(() -> new UsuarioNoExisteException(login));
                List<Pedido> pedidosDelUsuario = repository.findAllByUsuario(usuario)
                                .orElseThrow(() -> new PedidosNoEncontradosException(usuario.getLogin()));
                return pedidosDelUsuario;
        }

        @Transactional
        public void BorrarPedido(Long idPedido, String login) {
                Usuario usuario = usuarioRepository.findByLogin(login)
                                .orElseThrow(() -> new UsuarioNoExisteException(login));
                Pedido pedidoABorrar = repository.findByUsuarioAndIdPedido(usuario, idPedido)
                                .orElseThrow(() -> new PedidoNoEncontradoException(idPedido));
                repository.delete(pedidoABorrar);
        }

        private BigDecimal CalcularImporteRequest(List<PlatoPedido> platoPedidos, List<MenuPedido> menuPedidos) {
                BigDecimal importDecimal = new BigDecimal(0);
                if (platoPedidos != null) {
                        for (PlatoPedido plato : platoPedidos) {
                                Plato pl = platoRepository.findById(plato.getIdPlato())
                                                .orElseThrow(() -> new PlatoNoEncontradoException(plato.getIdPlato()));
                                importDecimal = importDecimal
                                                .add(pl.getPrecio().multiply(BigDecimal.valueOf(plato.getCantidad())));
                        }
                }
                if (menuPedidos != null) {
                        for (MenuPedido men : menuPedidos) {
                                importDecimal = importDecimal.add(BigDecimal.valueOf(15 * men.getCantidad()));
                        }
                }
                return importDecimal;
        }

}
