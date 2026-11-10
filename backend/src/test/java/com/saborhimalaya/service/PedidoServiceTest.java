package com.saborhimalaya.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.saborhimalaya.dto.EleccionesMenu;
import com.saborhimalaya.dto.MenuPedido;
import com.saborhimalaya.dto.PedidoRequest;
import com.saborhimalaya.dto.PlatoPedido;
import com.saborhimalaya.entity.Menu;
import com.saborhimalaya.entity.Pedido;
import com.saborhimalaya.exception.MenuNoConfiguradoException;
import com.saborhimalaya.exception.PedidoInvalidoException;
import com.saborhimalaya.exception.PlatoNoEncontradoException;

/**
 * PedidoService: pedido correcto, importes, pedido vacio, cantidades invalidas,
 * productos o
 * elecciones no permitidos y rollback completo si falla una linea.
 */
class PedidoServiceTest extends BasePedidoTest {
        @Autowired
        private PedidoService pedidoService;

        // ---- Pedido correcto y calculo de importes ----

        @Test
        void pedidoCorrecto_guardaPedidoLineasYEleccionesConElImporteTotal() {
                // 2 x curry (12,00) + 1 menu (15,00) = 39,00
                PedidoRequest request = pedido(
                                Arrays.asList(platoPedido(curry, 2)),
                                Arrays.asList(menuPedido(eleccionesValidas(), 1)));

                Pedido creado = pedidoService.AnadirPedido(request, LOGIN);

                Pedido guardado = pedidoRepository.findById(creado.getIdPedido()).orElseThrow();
                assertThat(guardado.getImporteTotal()).isEqualByComparingTo("39.00");
                assertThat(guardado.getEstado()).isEqualTo(Pedido.RECIBIDO);
                assertThat(pedidoRepository.count()).isEqualTo(1);
                assertThat(lineaPedidoRepository.count()).isEqualTo(2); // una de plato y una de menu
                assertThat(eleccionMenuRepository.count()).isEqualTo(4); // las cuatro elecciones del menu
        }

        @Test
        void importeTotal_sumaVariosPlatosYMenusConSusCantidades() {
                // 3 x momos (7,50) + 1 x curry (12,00) + 2 menus (15,00) = 22,50 + 12,00 +
                // 30,00 = 64,50
                PedidoRequest request = pedido(
                                Arrays.asList(platoPedido(momos, 3), platoPedido(curry, 1)),
                                Arrays.asList(menuPedido(eleccionesValidas(), 2)));

                Pedido creado = pedidoService.AnadirPedido(request, LOGIN);

                assertThat(creado.getImporteTotal()).isEqualByComparingTo("64.50");
                assertThat(lineaPedidoRepository.count()).isEqualTo(3);
        }

        // ---- Pedido vacio ----

        @Test
        void pedidoVacio_sinListas_seRechaza() {
                assertThatThrownBy(() -> pedidoService.AnadirPedido(new PedidoRequest(null, null), LOGIN))
                                .isInstanceOf(PedidoInvalidoException.class);
                assertNadaGuardado();
        }

        @Test
        void pedidoVacio_conListasVacias_seRechaza() {
                PedidoRequest request = pedido(new ArrayList<PlatoPedido>(), new ArrayList<MenuPedido>());

                assertThatThrownBy(() -> pedidoService.AnadirPedido(request, LOGIN))
                                .isInstanceOf(PedidoInvalidoException.class);
                assertNadaGuardado();
        }

        // ---- Cantidades invalidas ----

        @ParameterizedTest
        @ValueSource(ints = { 0, -1 })
        void cantidadInvalida_enUnPlato_seRechaza(int cantidad) {
                PedidoRequest request = pedido(Arrays.asList(platoPedido(curry, cantidad)), null);

                assertThatThrownBy(() -> pedidoService.AnadirPedido(request, LOGIN))
                                .isInstanceOf(PedidoInvalidoException.class);
                assertNadaGuardado();
        }

        @ParameterizedTest
        @ValueSource(ints = { 0, -3 })
        void cantidadInvalida_enUnMenu_seRechaza(int cantidad) {
                PedidoRequest request = pedido(null, Arrays.asList(menuPedido(eleccionesValidas(), cantidad)));

                assertThatThrownBy(() -> pedidoService.AnadirPedido(request, LOGIN))
                                .isInstanceOf(PedidoInvalidoException.class);
                assertNadaGuardado();
        }
        // ---- Productos o elecciones no permitidos ----

        @Test
        void platoInexistente_seRechaza() {
                PedidoRequest request = pedido(Arrays.asList(new PlatoPedido(9999L, 1)), null);

                assertThatThrownBy(() -> pedidoService.AnadirPedido(request, LOGIN))
                                .isInstanceOf(PlatoNoEncontradoException.class);
                assertNadaGuardado();
        }

        @Test
        void platoFueraDeCarta_seRechaza() {
                PedidoRequest request = pedido(Arrays.asList(platoPedido(platoRetirado, 1)), null);

                assertThatThrownBy(() -> pedidoService.AnadirPedido(request, LOGIN))
                                .isInstanceOf(PlatoNoEncontradoException.class);
                assertNadaGuardado();
        }

        @Test
        void eleccionQueNoEsOpcionDelMenu_seRechaza() {
                // curry existe y esta en la carta, pero no es una de las opciones de principal
                // del menu
                EleccionesMenu elecciones = new EleccionesMenu(
                                eleccion(momos, "Entrante"), eleccion(curry, "Principal"),
                                eleccion(kheer, "Postre"), eleccion(agua, "Bebida"));

                assertThatThrownBy(
                                () -> pedidoService.AnadirPedido(pedido(null, Arrays.asList(menuPedido(elecciones, 1))),
                                                LOGIN))
                                .isInstanceOf(PedidoInvalidoException.class);
                assertNadaGuardado();
        }

        // ---- Rollback completo si falla una linea ----
        // En todos estos casos alguna linea ya se ha guardado cuando se produce el
        // fallo:
        // si la transaccion no se deshiciera, quedarian pedido y lineas huerfanos.

        @Test
        void rollback_siFallaLaSegundaLineaDePlato() {
                // curry es valido (su linea se guarda) y platoRetirado falla despues
                PedidoRequest request = pedido(
                                Arrays.asList(platoPedido(curry, 1), platoPedido(platoRetirado, 1)), null);

                assertThatThrownBy(() -> pedidoService.AnadirPedido(request, LOGIN))
                                .isInstanceOf(PlatoNoEncontradoException.class);
                assertNadaGuardado();
        }

        @Test
        void rollback_siFallaLaEleccionDeUnMenuTrasGuardarLaLineaDePlato() {
                EleccionesMenu eleccionesInvalidas = new EleccionesMenu(
                                eleccion(momos, "Entrante"), eleccion(curry, "Principal"),
                                eleccion(kheer, "Postre"), eleccion(agua, "Bebida"));
                PedidoRequest request = pedido(
                                Arrays.asList(platoPedido(curry, 1)),
                                Arrays.asList(menuPedido(eleccionesInvalidas, 1)));

                assertThatThrownBy(() -> pedidoService.AnadirPedido(request, LOGIN))
                                .isInstanceOf(PedidoInvalidoException.class);
                assertNadaGuardado(); // ni el pedido, ni la linea de plato, ni la linea de menu
        }

        @Test
        void rollback_siFallaUnSegundoMenuTrasGuardarElPrimero() {
                EleccionesMenu eleccionesInvalidas = new EleccionesMenu(
                                eleccion(momos, "Entrante"), eleccion(dalBhat, "Principal"),
                                eleccion(kheer, "Postre"), eleccion(curry, "Bebida")); // curry no es una bebida
                PedidoRequest request = pedido(null, Arrays.asList(
                                menuPedido(eleccionesValidas(), 1),
                                menuPedido(eleccionesInvalidas, 1)));

                assertThatThrownBy(() -> pedidoService.AnadirPedido(request, LOGIN))
                                .isInstanceOf(PedidoInvalidoException.class);
                assertNadaGuardado(); // tampoco las 4 elecciones del primer menu
        }
}