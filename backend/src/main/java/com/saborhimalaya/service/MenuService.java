package com.saborhimalaya.service;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

// US12, US13
@Service
public class MenuService {

    public static final BigDecimal PRECIO_MENU = new BigDecimal("15.00");

    // CA12.1-CA12.3: número de platos de cada tipo de opción
    public static final int NUM_ENTRANTES = 3;
    public static final int NUM_PRINCIPALES = 3;
    public static final int NUM_POSTRES = 2;

    // US13: filtro por origen del plato
    public static final String ORIGEN_CARTA = "carta";
    public static final String ORIGEN_FUERA = "fuera";

    // Categorías de la carta que determinan en qué tipo de opción puede ir un plato
    private static final Set<Categoria> CATEGORIAS_ENTRANTE = EnumSet.of(Categoria.ENTRANTES, Categoria.ENSALADAS);
    private static final Set<Categoria> CATEGORIAS_POSTRE = EnumSet.of(Categoria.POSTRES);
    private static final Set<Categoria> CATEGORIAS_NO_PRINCIPAL = EnumSet.of(Categoria.ENTRANTES, Categoria.ENSALADAS,
            Categoria.POSTRES, Categoria.BEBIDAS);

    private final MenuRepository menuRepository;
    private final OpcionMenuRepository opcionMenuRepository;
    private final PlatoRepository platoRepository;

    public MenuService(MenuRepository menuRepository, OpcionMenuRepository opcionMenuRepository,
            PlatoRepository platoRepository) {
        this.menuRepository = menuRepository;
        this.opcionMenuRepository = opcionMenuRepository;
        this.platoRepository = platoRepository;
    }

    // US12: guarda el menú del día con sus 3 entrantes, 3 principales y 2 postres.
    // Si ya había un menú para esa fecha, se sobrescriben sus opciones.
    @Transactional
    public Menu configurarMenu(ConfigurarMenuRequest request) {
        comprobarMenuCompleto(request);

        Menu menu = menuRepository.findByFecha(request.getFecha())
                .orElseGet(() -> new Menu(request.getFecha(), PRECIO_MENU, false));

        if (menu.getIdMenu() != null) {
            opcionMenuRepository.deleteByMenu(menu);
            // Hibernate ejecuta los INSERT antes que los DELETE; sin el flush, las opciones
            // nuevas
            // chocarían con las antiguas en las restricciones únicas de opcion_menu
            opcionMenuRepository.flush();
        }

        menu.setPrecio(PRECIO_MENU);
        menu.setConfigurado(true);
        menu = menuRepository.save(menu);

        guardarOpciones(menu, request.getEntrantes(), TipoOpcion.ENTRANTE);
        guardarOpciones(menu, request.getPrincipales(), TipoOpcion.PRINCIPAL);
        guardarOpciones(menu, request.getPostres(), TipoOpcion.POSTRE);

        return menu;
    }

    // US13: platos del restaurante (en la carta o fuera de ella) que se pueden
    // elegir para el menú.
    // tipoOpcion y origen son opcionales; si se omiten no se filtra por ellos.
    @Transactional(readOnly = true)
    public List<PlatoDto> listarPlatosParaMenu(String tipoOpcion, String origen) {
        TipoOpcion tipo = tipoOpcion == null ? null : comprobarTipoOpcion(tipoOpcion);
        Boolean enCarta = origen == null ? null : comprobarOrigen(origen);

        return platoRepository.findAllByOrderByCategoriaAscNombreAsc().stream()
                .filter(plato -> enCarta == null || plato.isEnCarta() == enCarta)
                .filter(plato -> tipo == null || admiteCategoria(tipo, plato.getCategoria()))
                .map(PlatoDto::desde)
                .toList();
    }

    // CA13.1-CA13.3: qué categorías de plato admite cada tipo de opción del menú
    public static boolean admiteCategoria(TipoOpcion tipoOpcion, Categoria categoria) {
        if (tipoOpcion == null || categoria == null) {
            return false;
        }
        return switch (tipoOpcion) {
            case ENTRANTE -> CATEGORIAS_ENTRANTE.contains(categoria);
            case POSTRE -> CATEGORIAS_POSTRE.contains(categoria);
            case PRINCIPAL -> !CATEGORIAS_NO_PRINCIPAL.contains(categoria);
        };
    }

    private void guardarOpciones(Menu menu, List<Long> idsPlatos, TipoOpcion tipoOpcion) {
        for (int i = 0; i < idsPlatos.size(); i++) {
            Long idPlato = idsPlatos.get(i);
            Plato plato = platoRepository.findById(idPlato)
                    .orElseThrow(() -> new PlatoNoEncontradoException(idPlato));
            if (!admiteCategoria(tipoOpcion, plato.getCategoria())) {
                throw new DatosMenuNoValidosException(
                        "El plato " + plato.getNombre() + " (" + plato.getCategoria().getNombre()
                                + ") no se puede elegir como " + tipoOpcion.getNombre().toLowerCase(Locale.ROOT));
            }
            opcionMenuRepository.save(new OpcionMenu(menu, plato, tipoOpcion, i + 1));
        }
    }

    // CA12.1-CA12.4: el menú solo queda configurado si está completo, aunque no se
    // llame desde el controlador
    private static void comprobarMenuCompleto(ConfigurarMenuRequest request) {
        if (request.getFecha() == null) {
            throw new DatosMenuNoValidosException("Falta la fecha del menú");
        }
        comprobarSeleccion(request.getEntrantes(), NUM_ENTRANTES, "entrantes");
        comprobarSeleccion(request.getPrincipales(), NUM_PRINCIPALES, "principales");
        comprobarSeleccion(request.getPostres(), NUM_POSTRES, "postres");
    }

    private static void comprobarSeleccion(List<Long> idsPlatos, int cantidad, String grupo) {
        if (idsPlatos == null || idsPlatos.size() != cantidad) {
            throw new DatosMenuNoValidosException("El menú necesita exactamente " + cantidad + " " + grupo);
        }
        if (idsPlatos.stream().anyMatch(Objects::isNull)) {
            throw new DatosMenuNoValidosException("Hay un plato sin identificar en los " + grupo);
        }
        if (new HashSet<>(idsPlatos).size() != idsPlatos.size()) {
            throw new DatosMenuNoValidosException("No se puede repetir el mismo plato en los " + grupo);
        }
    }

    private static TipoOpcion comprobarTipoOpcion(String tipoOpcion) {
        try {
            return TipoOpcion.desde(tipoOpcion);
        } catch (IllegalArgumentException e) {
            throw new DatosMenuNoValidosException(e.getMessage());
        }
    }

    private static boolean comprobarOrigen(String origen) {
        String valor = origen.trim().toLowerCase(Locale.ROOT);
        if (valor.equals(ORIGEN_CARTA)) {
            return true;
        }
        if (valor.equals(ORIGEN_FUERA)) {
            return false;
        }
        throw new DatosMenuNoValidosException("Origen no válido: " + origen);
    }
}
