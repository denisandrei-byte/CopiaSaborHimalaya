package com.saborhimalaya.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.saborhimalaya.dto.MenuPedido;
import com.saborhimalaya.dto.PlatoPedido;
import com.saborhimalaya.entity.LineaPedido;
import com.saborhimalaya.entity.Menu;
import com.saborhimalaya.entity.Pedido;
import com.saborhimalaya.exception.MenuNoConfiguradoException;
import com.saborhimalaya.exception.PlatoNoEncontradoException;

/**
 * LineaPedidoService: precio unitario con cantidad mayor que uno y productos no
 * permitidos.
 */
class LineaPedidoServiceTest extends BasePedidoTest {
    @Autowired
    private LineaPedidoService lineaPedidoService;

    @Test
    void lineaDePlato_precioUnitarioEsElDeUnaUnidadConCantidadMayorQueUno() {
        Pedido pedido = pedidoVacioGuardado();

        lineaPedidoService.AnadirLineaPedidoPlato(platoPedido(curry, 3), pedido);

        List<LineaPedido> lineas = lineaPedidoRepository.findAll();
        assertThat(lineas).hasSize(1);
        assertThat(lineas.get(0).getCantidad()).isEqualTo(3);
        assertThat(lineas.get(0).getPrecioUnitario()).isEqualByComparingTo("12.00"); // no 36,00
    }

    @Test
    void lineaDeMenu_precioUnitarioEsElDelMenuYGuardaLasCuatroElecciones() {
        Pedido pedido = pedidoVacioGuardado();

        lineaPedidoService.AnadirLineaPedidoMenu(menuPedido(eleccionesValidas(), 2), pedido);

        List<LineaPedido> lineas = lineaPedidoRepository.findAll();
        assertThat(lineas).hasSize(1);
        assertThat(lineas.get(0).getCantidad()).isEqualTo(2);
        assertThat(lineas.get(0).getPrecioUnitario()).isEqualByComparingTo("15.00"); // no 30,00
        assertThat(eleccionMenuRepository.count()).isEqualTo(4);
    }

    @Test
    void lineaDePlato_platoFueraDeCarta_noGuardaNada() {
        Pedido pedido = pedidoVacioGuardado();

        assertThatThrownBy(() -> lineaPedidoService.AnadirLineaPedidoPlato(platoPedido(platoRetirado, 1), pedido))
                .isInstanceOf(PlatoNoEncontradoException.class);
        assertThat(lineaPedidoRepository.count()).isEqualTo(0);
    }

    @Test
    void lineaDePlato_platoInexistente_noGuardaNada() {
        Pedido pedido = pedidoVacioGuardado();

        assertThatThrownBy(() -> lineaPedidoService.AnadirLineaPedidoPlato(new PlatoPedido(9999L, 1), pedido))
                .isInstanceOf(PlatoNoEncontradoException.class);
        assertThat(lineaPedidoRepository.count()).isEqualTo(0);
    }
}