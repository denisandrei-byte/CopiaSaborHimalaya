package com.saborhimalaya.service;

import org.springframework.stereotype.Service;

import com.saborhimalaya.dto.EleccionMenuCustom;
import com.saborhimalaya.dto.MenuPedido;
import com.saborhimalaya.entity.EleccionMenu;
import com.saborhimalaya.entity.LineaPedido;
import com.saborhimalaya.entity.Plato;
import com.saborhimalaya.exception.EleccionesMenuInvalidasException;
import com.saborhimalaya.exception.PedidoInvalidoException;
import com.saborhimalaya.exception.PlatoNoEncontradoException;
import com.saborhimalaya.repository.EleccionMenuRepository;
import com.saborhimalaya.repository.PlatoRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EleccionMenuService {
        private final PlatoRepository platoRepository;
        private final EleccionMenuRepository repository;

        @Transactional
        public void AnadirOpcionesMenu(LineaPedido nuevLineaPedido, MenuPedido menuPedido) {
                if (!menuPedido.getElecciones().sonValidasLasElecciones())
                        throw new PedidoInvalidoException();
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
                if (!(platoMenu.getCategoria().getNombre().toLowerCase().equals(eleccion.getOpcion().toLowerCase())))
                        throw new PedidoInvalidoException();
                EleccionMenu eleccionMenu = new EleccionMenu(lineaPedido,
                                platoMenu,
                                eleccion.getOpcion());

                repository.save(eleccionMenu);
        }
}
