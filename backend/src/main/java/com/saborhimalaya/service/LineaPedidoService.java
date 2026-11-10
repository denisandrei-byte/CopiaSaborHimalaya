package com.saborhimalaya.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.saborhimalaya.dto.MenuPedido;
import com.saborhimalaya.dto.PlatoPedido;
import com.saborhimalaya.entity.LineaPedido;
import com.saborhimalaya.entity.Menu;
import com.saborhimalaya.entity.Pedido;
import com.saborhimalaya.entity.Plato;
import com.saborhimalaya.exception.MenuNoConfiguradoException;
import com.saborhimalaya.exception.PedidoInvalidoException;
import com.saborhimalaya.exception.PlatoNoEncontradoException;
import com.saborhimalaya.repository.LineaPedidoRepository;
import com.saborhimalaya.repository.MenuRepository;
import com.saborhimalaya.repository.PlatoRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class LineaPedidoService {
        private final PlatoRepository platoRepository;
        private final LineaPedidoRepository repository;
        private final MenuRepository menuRepository;
        private final EleccionMenuService opcionMenuService;

        @Transactional
        public void AnadirLineaPedidoMenu(MenuPedido menu, Pedido pedido) {
                if (menu.getCantidad() == null || menu.getCantidad() <= 0)
                        throw new PedidoInvalidoException("porque la cantidad de menus es invalida");
                BigDecimal importe = new BigDecimal(0);
                Menu menuOr = menuRepository.findByFechaAndConfigurado(menu.getFechaMenu(), true)
                                .orElseThrow(() -> new MenuNoConfiguradoException());
                LineaPedido nuevaLineaPedido = new LineaPedido(pedido, null,
                                menuOr,
                                menu.getCantidad(),
                                importe.add(new BigDecimal(15)));
                LineaPedido lineaPedidoCreada = repository.save(nuevaLineaPedido);
                opcionMenuService.AnadirOpcionesMenu(lineaPedidoCreada, menu);
        }

        @Transactional

        public void AnadirLineaPedidoPlato(PlatoPedido platoPedido, Pedido pedido) {
                if (platoPedido.getCantidad() == null || platoPedido.getCantidad() <= 0)
                        throw new PedidoInvalidoException("porque la cantidad de platos es invalida");
                BigDecimal importe = new BigDecimal(0);
                Plato platoOr;
                LineaPedido nuevaLineaPedido = new LineaPedido(pedido,
                                platoOr = platoRepository.findByIdPlatoAndEnCarta(platoPedido.getIdPlato(), true)
                                                .orElseThrow(() -> new PlatoNoEncontradoException(
                                                                platoPedido.getIdPlato())),
                                null,
                                platoPedido.getCantidad(),
                                importe.add(platoOr.getPrecio()));
                repository.save(nuevaLineaPedido);

        }
}
