package com.saborhimalaya.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.saborhimalaya.dto.EleccionMenuCustom;
import com.saborhimalaya.dto.EleccionesMenu;
import com.saborhimalaya.entity.EleccionMenu;
import com.saborhimalaya.entity.LineaPedido;
import com.saborhimalaya.exception.PedidoInvalidoException;
import com.saborhimalaya.exception.PlatoNoEncontradoException;

/**
 * EleccionMenuService: elecciones validas y elecciones no permitidas de un
 * menu.
 */
class EleccionMenuServiceTest extends BasePedidoTest {
        @Autowired
        private EleccionMenuService eleccionMenuService;

        @Test
        void elecciones_validas_seGuardanLasCuatroConSuTipo() {
                LineaPedido lineaMenu = lineaDeMenuGuardada();

                eleccionMenuService.AnadirOpcionesMenu(lineaMenu, menuPedido(eleccionesValidas(), 1));

                Set<String> tipos = eleccionMenuRepository.findAll().stream()
                                .map(EleccionMenu::getTipo)
                                .collect(Collectors.toSet());
                assertThat(eleccionMenuRepository.count()).isEqualTo(4);
                assertThat(tipos).containsExactlyInAnyOrder("Entrante", "Principal", "Postre", "Bebida");
        }

        @Test
        void elecciones_platoInexistente_seRechazaSinGuardarNinguna() {
                LineaPedido lineaMenu = lineaDeMenuGuardada();
                EleccionesMenu elecciones = new EleccionesMenu(
                                eleccion(momos, "Entrante"), eleccion(dalBhat, "Principal"),
                                eleccion(kheer, "Postre"), new EleccionMenuCustom(9999L, "Bebidas"));

                assertThatThrownBy(() -> eleccionMenuService.AnadirOpcionesMenu(lineaMenu, menuPedido(elecciones, 1)))
                                .isInstanceOf(PlatoNoEncontradoException.class);
                assertThat(eleccionMenuRepository.count()).isEqualTo(0);
        }

        @Test
        void elecciones_tipoQueNoCoincideConElDelPlato_seRechaza() {
                LineaPedido lineaMenu = lineaDeMenuGuardada();
                // momos es un entrante del menu, pero se envia como "Principal"
                EleccionesMenu elecciones = new EleccionesMenu(
                                eleccion(dalBhat, "Entrante"), eleccion(momos, "Principal"),
                                eleccion(kheer, "Postre"), eleccion(agua, "Bebida"));
                assertThatThrownBy(() -> eleccionMenuService.AnadirOpcionesMenu(lineaMenu, menuPedido(elecciones, 1)))
                                .isInstanceOf(PedidoInvalidoException.class);
                assertThat(eleccionMenuRepository.count()).isEqualTo(0);
        }

        @Test
        void elecciones_repetirElMismoTipo_seRechaza() {
                LineaPedido lineaMenu = lineaDeMenuGuardada();
                EleccionesMenu elecciones = new EleccionesMenu(
                                eleccion(momos, "Entrantes"), eleccion(momos, "Entrantes"),
                                eleccion(momos, "Entrantes"), eleccion(momos, "Entrantes"));

                assertThatThrownBy(() -> eleccionMenuService.AnadirOpcionesMenu(lineaMenu, menuPedido(elecciones, 1)))
                                .isInstanceOf(PedidoInvalidoException.class);
                assertThat(eleccionMenuRepository.count()).isEqualTo(0);
        }

        @Test
        void elecciones_bebidaQueNoEsDeCategoriaBebidas_seRechaza() {
                LineaPedido lineaMenu = lineaDeMenuGuardada();
                EleccionesMenu elecciones = new EleccionesMenu(
                                eleccion(momos, "Entrante"), eleccion(dalBhat, "Principal"),
                                eleccion(kheer, "Postre"), eleccion(curry, "Bebida")); // curry no es una bebida

                assertThatThrownBy(() -> eleccionMenuService.AnadirOpcionesMenu(lineaMenu, menuPedido(elecciones, 1)))
                                .isInstanceOf(PedidoInvalidoException.class);
                assertThat(eleccionMenuRepository.count()).isEqualTo(0);
        }
}