package com.saborhimalaya;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.saborhimalaya.entity.Categoria;
import com.saborhimalaya.entity.EleccionMenu;
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

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Testcontainers
class PersistenceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private LineaPedidoRepository lineaPedidoRepository;

    @Autowired
    private PlatoRepository platoRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private OpcionMenuRepository opcionMenuRepository;

    @Autowired
    private EleccionMenuRepository eleccionMenuRepository;

    @Test
    void guardaYRecuperaUsuario() {
        Usuario usuario = usuarioRepository.save(new Usuario("Lucas", "Calle Nepal 1", "lucas", "lucas@example.com", "secreto", true));

        Usuario recuperado = usuarioRepository.findById(usuario.getIdUsuario()).orElseThrow();

        assertThat(recuperado.getNombre()).isEqualTo("Lucas");
        assertThat(recuperado.getDireccion()).isEqualTo("Calle Nepal 1");
        assertThat(recuperado.getEmail()).isEqualTo("lucas@example.com");
        assertThat(recuperado.isActivo()).isTrue();
    }

    @Test
    void guardaPedidoConPlatoMenuYElecciones() {
        Usuario usuario = usuarioRepository.save(new Usuario("Ana", "Calle Everest 2", "ana", "ana@example.com", "secreto", true));
        Plato momo = platoRepository.save(new Plato("Momo", "Momo de pollo", new BigDecimal("8.50"), Categoria.ENTRANTES, true));
        Plato curry = platoRepository.save(new Plato("Curry", "Curry nepalí", new BigDecimal("10.25"), Categoria.PRINCIPALES, true));
        Menu menu = menuRepository.save(new Menu(LocalDate.of(2026, 11, 2), new BigDecimal("15.00"), true));
        OpcionMenu opcion = opcionMenuRepository.save(new OpcionMenu(menu, momo, TipoOpcion.ENTRANTE, 1));
        Pedido pedido = pedidoRepository.save(new Pedido(usuario, new BigDecimal("32.00"), "PENDIENTE", LocalDateTime.of(2026, 11, 2, 12, 30)));
        LineaPedido lineaPlato = lineaPedidoRepository.save(new LineaPedido(pedido, curry, null, 1, new BigDecimal("10.25")));
        LineaPedido lineaMenu = lineaPedidoRepository.save(new LineaPedido(pedido, null, menu, 1, new BigDecimal("15.00")));
        EleccionMenu eleccion = eleccionMenuRepository.save(new EleccionMenu(lineaMenu, momo, "Entrante"));

        Pedido pedidoRecuperado = pedidoRepository.findById(pedido.getIdPedido()).orElseThrow();
        LineaPedido lineaPlatoRecuperada = lineaPedidoRepository.findById(lineaPlato.getIdLinea()).orElseThrow();
        LineaPedido lineaMenuRecuperada = lineaPedidoRepository.findById(lineaMenu.getIdLinea()).orElseThrow();
        OpcionMenu opcionRecuperada = opcionMenuRepository.findById(opcion.getIdOpcion()).orElseThrow();
        EleccionMenu eleccionRecuperada = eleccionMenuRepository.findById(eleccion.getIdEleccion()).orElseThrow();

        assertThat(pedidoRecuperado.getUsuario().getIdUsuario()).isEqualTo(usuario.getIdUsuario());
        assertThat(pedidoRecuperado.getImporteTotal()).isEqualByComparingTo("32.00");
        assertThat(pedidoRecuperado.getFechaHoraCreacion()).isEqualTo(LocalDateTime.of(2026, 11, 2, 12, 30));
        assertThat(lineaPlatoRecuperada.getPlato().getIdPlato()).isEqualTo(curry.getIdPlato());
        assertThat(lineaMenuRecuperada.getMenu().getIdMenu()).isEqualTo(menu.getIdMenu());
        assertThat(lineaMenuRecuperada.getPrecioUnitario()).isEqualByComparingTo("15.00");
        assertThat(opcionRecuperada.getPlato().getIdPlato()).isEqualTo(momo.getIdPlato());
        assertThat(opcionRecuperada.getOrden()).isEqualTo(1);
        assertThat(eleccionRecuperada.getLineaPedido().getIdLinea()).isEqualTo(lineaMenu.getIdLinea());
        assertThat(eleccionRecuperada.getPlato().getIdPlato()).isEqualTo(momo.getIdPlato());
    }

    @Test
    void eliminaUsuario() {
        Usuario usuario = usuarioRepository.save(new Usuario("Berta", "Calle Katmandú 3", "berta", "berta@example.com", "secreto", false));
        Long idUsuario = usuario.getIdUsuario();

        usuarioRepository.deleteById(idUsuario);

        assertThat(usuarioRepository.existsById(idUsuario)).isFalse();
    }
}
