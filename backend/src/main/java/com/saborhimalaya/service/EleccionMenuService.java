package com.saborhimalaya.service;

import org.springframework.stereotype.Service;

import com.saborhimalaya.dto.EleccionMenuCustom;
import com.saborhimalaya.dto.MenuPedido;
import com.saborhimalaya.entity.EleccionMenu;
import com.saborhimalaya.entity.LineaPedido;
import com.saborhimalaya.entity.OpcionMenu;
import com.saborhimalaya.entity.Plato;
import com.saborhimalaya.exception.EleccionesMenuInvalidasException;
import com.saborhimalaya.exception.PedidoInvalidoException;
import com.saborhimalaya.exception.PlatoNoEncontradoException;
import com.saborhimalaya.repository.EleccionMenuRepository;
import com.saborhimalaya.repository.OpcionMenuRepository;
import com.saborhimalaya.repository.PlatoRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EleccionMenuService {
        private final PlatoRepository platoRepository;
        private final EleccionMenuRepository repository;
        private final OpcionMenuRepository opcionMenuRepository;

        @Transactional
        public void AnadirOpcionesMenu(LineaPedido nuevLineaPedido, MenuPedido menuPedido) {
                if (!menuPedido.getElecciones().sonValidasLasElecciones())
                        throw new PedidoInvalidoException("Alguna eleccion del menú es invalida");
                EleccionMenuCustom eleccion1 = menuPedido.getElecciones().getEleccionMenu1();
                EleccionMenuCustom eleccion2 = menuPedido.getElecciones().getEleccionMenu2();
                EleccionMenuCustom eleccion3 = menuPedido.getElecciones().getEleccionMenu3();
                EleccionMenuCustom eleccion4 = menuPedido.getElecciones().getEleccionMenu4();
                AnadirEleccion(nuevLineaPedido, eleccion1);
                AnadirEleccion(nuevLineaPedido, eleccion2);
                AnadirEleccion(nuevLineaPedido, eleccion3);
                AnadirEleccion(nuevLineaPedido, eleccion4);
        }

        private void AnadirEleccion(LineaPedido lineaPedido, EleccionMenuCustom eleccion) {
                Plato platoMenu = platoRepository.findById(eleccion.getIdPlato())
                                .orElseThrow(() -> new PlatoNoEncontradoException(
                                                eleccion.getIdPlato()));
                if (!eleccion.getOpcion().equals("Bebida")) {
                        OpcionMenu opcionMenu = opcionMenuRepository
                                        .findByMenuAndPlato(lineaPedido.getMenu(), platoMenu)
                                        .orElseThrow(() -> new PedidoInvalidoException(
                                                        "No existe algun plato elegido en el menú"));

                        if (!eleccion.getOpcion().toLowerCase()
                                        .equals(opcionMenu.getTipoOpcion().getNombre().toLowerCase()))
                                throw new PedidoInvalidoException(
                                                "La categoria del plato seleccionado no coincide con la del menú");
                } else {
                        if (!eleccion.compararEntrePlatoYEleccion(platoMenu.getCategoria()) || !platoMenu.isEnCarta())
                                throw new PedidoInvalidoException(
                                                "porque " + platoMenu.getNombre()
                                                                + " no es una bebida o no esta en la carta");
                }

                EleccionMenu eleccionMenu = new EleccionMenu(lineaPedido,
                                platoMenu,
                                eleccion.getOpcion());

                repository.save(eleccionMenu);
        }
}
