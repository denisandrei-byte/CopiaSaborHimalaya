package com.saborhimalaya.service;

import org.springframework.stereotype.Service;

import com.saborhimalaya.dto.EleccionMenuCustom;
import com.saborhimalaya.dto.MenuPedido;
import com.saborhimalaya.entity.EleccionMenu;
import com.saborhimalaya.entity.LineaPedido;
import com.saborhimalaya.exception.PlatoNoEncontradoException;
import com.saborhimalaya.repository.EleccionMenuRepository;
import com.saborhimalaya.repository.PlatoRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class EleccionMenuService {
        private final PlatoRepository platoRepository;
        private final EleccionMenuRepository repository;

        public void AnadirOpcionesMenu(LineaPedido nuevLineaPedido, MenuPedido menuPedido) {
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
                EleccionMenu eleccionMenu = new EleccionMenu(lineaPedido,
                                platoRepository.findById(eleccion.getIdPlato())
                                                .orElseThrow(() -> new PlatoNoEncontradoException(
                                                                eleccion.getIdPlato())),
                                eleccion.getOpcion());
                repository.save(eleccionMenu);
        }
}
