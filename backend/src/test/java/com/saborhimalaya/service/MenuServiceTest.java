package com.saborhimalaya.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.saborhimalaya.dto.ConfigurarMenuRequest;
import com.saborhimalaya.dto.PlatoDto;
import com.saborhimalaya.entity.Categoria;
import com.saborhimalaya.entity.Menu;
import com.saborhimalaya.entity.OpcionMenu;
import com.saborhimalaya.entity.Plato;
import com.saborhimalaya.entity.TipoOpcion;
import com.saborhimalaya.exception.DatosMenuNoValidosException;
import com.saborhimalaya.exception.PlatoNoEncontradoException;
import com.saborhimalaya.repository.MenuRepository;
import com.saborhimalaya.repository.OpcionMenuRepository;
import com.saborhimalaya.repository.PlatoRepository;

// Tests de US12 y US13
@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Testcontainers
@Import(MenuService.class)
class MenuServiceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    private static final LocalDate FECHA = LocalDate.of(2026, 11, 5);

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private OpcionMenuRepository opcionMenuRepository;

    @Autowired
    private PlatoRepository platoRepository;

    private List<Long> entrantes;
    private List<Long> principales;
    private List<Long> postres;
    private Long bebida;
    private Long principalFueraDeCarta;

    @BeforeEach
    void crearPlatos() {
        entrantes = List.of(
                guardarPlato("Momos vegetales", Categoria.ENTRANTES),
                guardarPlato("Pakora", Categoria.ENTRANTES),
                guardarPlato("Ensalada nepalí", Categoria.ENSALADAS));
        principales = List.of(
                guardarPlato("Dal bhat", Categoria.PRINCIPALES),
                guardarPlato("Curry de pollo", Categoria.PRINCIPALES),
                guardarPlato("Thukpa", Categoria.PRINCIPALES));
        postres = List.of(
                guardarPlato("Kheer", Categoria.POSTRES),
                guardarPlato("Sel roti", Categoria.POSTRES));
        bebida = guardarPlato("Lassi", Categoria.BEBIDAS);
        principalFueraDeCarta = guardarPlato("Momos de cordero", Categoria.ESPECIALIDADES, false);
    }

    // CA12.4: con 3 entrantes, 3 principales y 2 postres el menú queda configurado
    @Test
    void configuraMenuNuevo() {
        Menu menu = menuService.configurarMenu(new ConfigurarMenuRequest(FECHA, entrantes, principales, postres));

        Menu guardado = menuRepository.findByFecha(FECHA).orElseThrow();
        assertThat(guardado.getIdMenu()).isEqualTo(menu.getIdMenu());
        assertThat(guardado.isConfigurado()).isTrue();
        assertThat(guardado.getPrecio()).isEqualByComparingTo("15.00");

        List<OpcionMenu> opciones = opcionMenuRepository.findByMenuOrderByTipoOpcionAscOrdenAsc(guardado);
        assertThat(opciones).hasSize(8);
        assertThat(idsDeTipo(opciones, TipoOpcion.ENTRANTE)).isEqualTo(entrantes);
        assertThat(idsDeTipo(opciones, TipoOpcion.PRINCIPAL)).isEqualTo(principales);
        assertThat(idsDeTipo(opciones, TipoOpcion.POSTRE)).isEqualTo(postres);
    }

    // CA12.1-CA12.3: las opciones se guardan con su posición dentro de cada tipo
    @Test
    void guardaElOrdenDeLasOpciones() {
        Menu menu = menuService.configurarMenu(new ConfigurarMenuRequest(FECHA, entrantes, principales, postres));

        List<OpcionMenu> opciones = opcionMenuRepository.findByMenuOrderByTipoOpcionAscOrdenAsc(menu);
        assertThat(opciones.stream().filter(o -> o.getTipoOpcion() == TipoOpcion.ENTRANTE).map(OpcionMenu::getOrden))
                .containsExactly(1, 2, 3);
        assertThat(opciones.stream().filter(o -> o.getTipoOpcion() == TipoOpcion.POSTRE).map(OpcionMenu::getOrden))
                .containsExactly(1, 2);
    }

    // Si ya hay menú ese día, se sobrescriben sus opciones manteniendo el mismo menú
    @Test
    void sobrescribeMenuExistente() {
        Menu primero = menuService.configurarMenu(new ConfigurarMenuRequest(FECHA, entrantes, principales, postres));

        List<Long> nuevosPostres = List.of(postres.get(1), postres.get(0));
        Menu segundo = menuService.configurarMenu(new ConfigurarMenuRequest(FECHA, entrantes, principales, nuevosPostres));

        assertThat(segundo.getIdMenu()).isEqualTo(primero.getIdMenu());
        assertThat(menuRepository.count()).isEqualTo(1);

        List<OpcionMenu> opciones = opcionMenuRepository.findByMenuOrderByTipoOpcionAscOrdenAsc(segundo);
        assertThat(opciones).hasSize(8);
        assertThat(idsDeTipo(opciones, TipoOpcion.POSTRE)).isEqualTo(nuevosPostres);
    }

    @Test
    void fallaSiUnPlatoNoExiste() {
        List<Long> postresConPlatoInexistente = List.of(postres.get(0), 999_999L);

        assertThatThrownBy(() -> menuService.configurarMenu(
                new ConfigurarMenuRequest(FECHA, entrantes, principales, postresConPlatoInexistente)))
                .isInstanceOf(PlatoNoEncontradoException.class)
                .hasMessageContaining("999999");
    }

    // CA13.2: entrantes = categorías entrantes o ensaladas
    @Test
    void listaEntrantesYEnsaladasComoEntrantes() {
        assertThat(idsDe(menuService.listarPlatosParaMenu(TipoOpcion.ENTRANTE.getNombre(), null)))
                .containsExactlyInAnyOrderElementsOf(entrantes);
    }

    // CA13.1: principales = categorías que no son entrantes, ensaladas, postres ni bebidas
    @Test
    void listaComoPrincipalesElRestoDeCategorias() {
        List<Long> esperados = new ArrayList<>(principales);
        esperados.add(principalFueraDeCarta);

        List<Long> ids = idsDe(menuService.listarPlatosParaMenu(TipoOpcion.PRINCIPAL.getNombre(), null));

        assertThat(ids).containsExactlyInAnyOrderElementsOf(esperados);
        assertThat(ids).doesNotContain(bebida);
    }

    // CA13.3: postres = categoría postres
    @Test
    void listaSoloPostresComoPostres() {
        assertThat(idsDe(menuService.listarPlatosParaMenu(TipoOpcion.POSTRE.getNombre(), null)))
                .containsExactlyInAnyOrderElementsOf(postres);
    }

    // CA13.4: se listan también los platos del restaurante que no están en la carta
    @Test
    void filtraPorOrigenDelPlato() {
        List<PlatoDto> fuera = menuService.listarPlatosParaMenu(null, MenuService.ORIGEN_FUERA);
        assertThat(idsDe(fuera)).containsExactly(principalFueraDeCarta);
        assertThat(fuera.get(0).enCarta()).isFalse();

        assertThat(idsDe(menuService.listarPlatosParaMenu(null, MenuService.ORIGEN_CARTA)))
                .hasSize(9)
                .doesNotContain(principalFueraDeCarta);

        assertThat(menuService.listarPlatosParaMenu(null, null)).hasSize(10);
    }

    @Test
    void aceptaTipoYOrigenSinDistinguirMayusculas() {
        assertThat(idsDe(menuService.listarPlatosParaMenu("principal", "FUERA")))
                .containsExactly(principalFueraDeCarta);
    }

    @Test
    void rechazaTipoUOrigenNoValidos() {
        assertThatThrownBy(() -> menuService.listarPlatosParaMenu("Bebida", null))
                .isInstanceOf(DatosMenuNoValidosException.class);
        assertThatThrownBy(() -> menuService.listarPlatosParaMenu(null, "otro"))
                .isInstanceOf(DatosMenuNoValidosException.class);
    }

    // CA13.4: un plato fuera de la carta se puede usar en el menú
    @Test
    void configuraMenuConPlatoFueraDeCarta() {
        List<Long> conFueraDeCarta = List.of(principales.get(0), principales.get(1), principalFueraDeCarta);

        Menu menu = menuService.configurarMenu(new ConfigurarMenuRequest(FECHA, entrantes, conFueraDeCarta, postres));

        List<OpcionMenu> opciones = opcionMenuRepository.findByMenuOrderByTipoOpcionAscOrdenAsc(menu);
        assertThat(idsDeTipo(opciones, TipoOpcion.PRINCIPAL)).isEqualTo(conFueraDeCarta);
    }

    // CA13.1-CA13.3: un plato no se puede elegir en un tipo de opción que no le corresponde
    @Test
    void rechazaPlatoDeCategoriaNoValida() {
        List<Long> conBebida = List.of(principales.get(0), principales.get(1), bebida);
        List<Long> conPostre = List.of(entrantes.get(0), entrantes.get(1), postres.get(0));

        assertThatThrownBy(() -> menuService.configurarMenu(
                new ConfigurarMenuRequest(FECHA, entrantes, conBebida, postres)))
                .isInstanceOf(DatosMenuNoValidosException.class)
                .hasMessageContaining("Lassi");
        assertThatThrownBy(() -> menuService.configurarMenu(
                new ConfigurarMenuRequest(FECHA, conPostre, principales, postres)))
                .isInstanceOf(DatosMenuNoValidosException.class)
                .hasMessageContaining("Kheer");
    }

    private Long guardarPlato(String nombre, Categoria categoria) {
        return guardarPlato(nombre, categoria, true);
    }

    private Long guardarPlato(String nombre, Categoria categoria, boolean enCarta) {
        return platoRepository.save(new Plato(nombre, "Descripción de " + nombre, new BigDecimal("9.00"), categoria, enCarta))
                .getIdPlato();
    }

    private List<Long> idsDe(List<PlatoDto> platos) {
        return platos.stream().map(PlatoDto::idPlato).toList();
    }

    private List<Long> idsDeTipo(List<OpcionMenu> opciones, TipoOpcion tipoOpcion) {
        return opciones.stream()
                .filter(o -> o.getTipoOpcion() == tipoOpcion)
                .map(o -> o.getPlato().getIdPlato())
                .toList();
    }
}
