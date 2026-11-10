package com.saborhimalaya.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;

import com.saborhimalaya.dto.EleccionMenuCustom;
import com.saborhimalaya.dto.EleccionesMenu;
import com.saborhimalaya.dto.MenuPedido;
import com.saborhimalaya.dto.PedidoRequest;
import com.saborhimalaya.dto.PlatoPedido;
import com.saborhimalaya.entity.Categoria;
import com.saborhimalaya.entity.LineaPedido;
import com.saborhimalaya.entity.Menu;
import com.saborhimalaya.entity.OpcionMenu;
import com.saborhimalaya.entity.Pedido;
import com.saborhimalaya.entity.Plato;
import com.saborhimalaya.entity.TipoOpcion;
import com.saborhimalaya.entity.Usuario;
import com.saborhimalaya.repository.EleccionMenuRepository;
import com.saborhimalaya.repository.LineaPedidoRepository;
import com.saborhimalaya.repository.MenuRepository;
import com.saborhimalaya.repository.OpcionMenuRepository;
import com.saborhimalaya.repository.PedidoRepository;
import com.saborhimalaya.repository.PlatoRepository;
import com.saborhimalaya.repository.UsuarioRepository;

/**
 * Base comun de los tests de PedidoService, LineaPedidoService y
 * EleccionMenuService.
 *
 * - Usa una base de datos PostgreSQL real (Testcontainers), igual que
 * PersistenceTest.
 * - El contenedor se arranca una sola vez para todas las clases de test que
 * heredan de esta
 * (si cada clase lo reiniciara, Spring reutilizaria un contexto apuntando a un
 * puerto antiguo).
 * - Las clases hijas NO deben llevar @Transactional: asi cada servicio abre y
 * cierra su propia
 * transaccion y los tests de rollback comprueban de verdad que no queda nada
 * guardado.
 * - Antes de cada test se vacian las tablas y se recrean los mismos datos de
 * prueba.
 */
@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
abstract class BasePedidoTest {

    @ServiceConnection
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    static {
        postgres.start();
    }

    protected static final LocalDate FECHA_MENU = LocalDate.of(2026, 11, 10);
    protected static final String LOGIN = "cliente";

    @Autowired
    protected UsuarioRepository usuarioRepository;
    @Autowired
    protected PlatoRepository platoRepository;
    @Autowired
    protected MenuRepository menuRepository;
    @Autowired
    protected OpcionMenuRepository opcionMenuRepository;
    @Autowired
    protected PedidoRepository pedidoRepository;
    @Autowired
    protected LineaPedidoRepository lineaPedidoRepository;
    @Autowired
    protected EleccionMenuRepository eleccionMenuRepository;

    protected Usuario usuario;
    protected Menu menu; // menu del dia configurado: 15,00 EUR
    protected Plato momos; // entrante del menu, 7,50
    protected Plato dalBhat; // principal del menu, 11,00
    protected Plato kheer; // postre del menu, 5,00
    protected Plato agua; // bebida de la carta (no forma parte de las opciones del menu), 2,00
    protected Plato curry; // plato de la carta que NO es opcion del menu, 12,00
    protected Plato platoRetirado; // existe, pero ya no esta en la carta, 9,00

    @BeforeEach
    void prepararDatos() {
        // Orden inverso a las claves foraneas
        eleccionMenuRepository.deleteAll();
        lineaPedidoRepository.deleteAll();
        pedidoRepository.deleteAll();
        opcionMenuRepository.deleteAll();
        menuRepository.deleteAll();
        platoRepository.deleteAll();
        usuarioRepository.deleteAll();

        usuario = usuarioRepository.save(
                new Usuario("Cliente", "Calle Everest 2", LOGIN, "cliente@example.com", "secreto", true));

        momos = platoRepository.save(new Plato("Momos vegetales", "Empanadillas al vapor", new BigDecimal("7.50"),
                Categoria.ENTRANTES, true));
        dalBhat = platoRepository.save(new Plato("Dal bhat", "Lentejas con arroz", new BigDecimal("11.00"),
                Categoria.PRINCIPALES, true));
        kheer = platoRepository.save(new Plato("Kheer", "Arroz con leche", new BigDecimal("5.00"),
                Categoria.POSTRES, true));
        agua = platoRepository.save(new Plato("Agua mineral", "Botella de 50 cl", new BigDecimal("2.00"),
                Categoria.BEBIDAS, true));
        curry = platoRepository.save(new Plato("Pollo al curry", "Con arroz basmati", new BigDecimal("12.00"),
                Categoria.PRINCIPALES, true));
        platoRetirado = platoRepository.save(new Plato("Plato retirado", "Ya no se sirve", new BigDecimal("9.00"),
                Categoria.PRINCIPALES, false));

        menu = menuRepository.save(new Menu(FECHA_MENU, new BigDecimal("15.00"), true));
        opcionMenuRepository.save(new OpcionMenu(menu, momos, TipoOpcion.ENTRANTE, 1));
        opcionMenuRepository.save(new OpcionMenu(menu, dalBhat, TipoOpcion.PRINCIPAL, 1));
        opcionMenuRepository.save(new OpcionMenu(menu, kheer, TipoOpcion.POSTRE, 1));
    }

    // ---------------------------------------------------------------------
    // Utilidades compartidas
    // ---------------------------------------------------------------------

    protected EleccionMenuCustom eleccion(Plato plato, String opcion) {
        return new EleccionMenuCustom(plato.getIdPlato(), opcion);
    }

    protected EleccionesMenu eleccionesValidas() {
        return new EleccionesMenu(
                eleccion(momos, "Entrante"), eleccion(dalBhat, "Principal"),
                eleccion(kheer, "Postre"), eleccion(agua, "Bebida"));
    }

    protected PlatoPedido platoPedido(Plato plato, int cantidad) {
        return new PlatoPedido(plato.getIdPlato(), cantidad);
    }

    // ADAPTA: estos dos metodos son los unicos que dependen de como identificas el
    // menu
    // en MenuPedido. Aqui se asume new MenuPedido(LocalDate fechaMenu, elecciones,
    // cantidad).
    // Si vuelves a idMenu, usa menu.getIdMenu() en menuPedido(...) y un id
    // inexistente
    // (por ejemplo 9999L) en los tests que usan menuPedidoEnFecha(otroDia, ...).
    protected MenuPedido menuPedidoEnFecha(EleccionesMenu elecciones, int cantidad) {
        return new MenuPedido(elecciones, cantidad);
    }

    protected MenuPedido menuPedido(EleccionesMenu elecciones, int cantidad) {
        return menuPedidoEnFecha(elecciones, cantidad);
    }

    // PedidoRequest(menus, platos): el orden de los argumentos es el de los campos
    // de la clase
    protected PedidoRequest pedido(List<PlatoPedido> platos, List<MenuPedido> menus) {
        return new PedidoRequest(menus, platos);
    }

    protected Pedido pedidoVacioGuardado() {
        return pedidoRepository.save(
                new Pedido(usuario, BigDecimal.ZERO, Pedido.EN_PREPARACION, LocalDateTime.now()));
    }

    protected LineaPedido lineaDeMenuGuardada() {
        Pedido pedido = pedidoVacioGuardado();
        return lineaPedidoRepository.save(new LineaPedido(pedido, null, menu, 1, menu.getPrecio()));
    }

    protected void assertNadaGuardado() {
        assertThat(pedidoRepository.count()).as("pedidos").isEqualTo(0);
        assertThat(lineaPedidoRepository.count()).as("lineas de pedido").isEqualTo(0);
        assertThat(eleccionMenuRepository.count()).as("elecciones de menu").isEqualTo(0);
    }
}
