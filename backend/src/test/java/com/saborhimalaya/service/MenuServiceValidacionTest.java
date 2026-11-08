package com.saborhimalaya.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.saborhimalaya.dto.ConfigurarMenuRequest;
import com.saborhimalaya.exception.DatosMenuNoValidosException;
import com.saborhimalaya.repository.MenuRepository;
import com.saborhimalaya.repository.OpcionMenuRepository;
import com.saborhimalaya.repository.PlatoRepository;

// Tests de US12: el servicio rechaza un menú incompleto sin pasar por el controlador
// y sin llegar a tocar la base de datos
class MenuServiceValidacionTest {

    private static final LocalDate FECHA = LocalDate.of(2026, 11, 5);
    private static final List<Long> ENTRANTES = List.of(1L, 2L, 3L);
    private static final List<Long> PRINCIPALES = List.of(4L, 5L, 6L);
    private static final List<Long> POSTRES = List.of(7L, 8L);

    private MenuRepository menuRepository;
    private OpcionMenuRepository opcionMenuRepository;
    private PlatoRepository platoRepository;
    private MenuService menuService;

    @BeforeEach
    void crearServicio() {
        menuRepository = mock(MenuRepository.class);
        opcionMenuRepository = mock(OpcionMenuRepository.class);
        platoRepository = mock(PlatoRepository.class);
        menuService = new MenuService(menuRepository, opcionMenuRepository, platoRepository);
    }

    @Test
    void rechazaMenuSinFecha() {
        assertRechazado(new ConfigurarMenuRequest(null, ENTRANTES, PRINCIPALES, POSTRES), "fecha");
    }

    // CA12.1
    @Test
    void rechazaSiNoHayTresEntrantes() {
        assertRechazado(new ConfigurarMenuRequest(FECHA, List.of(1L, 2L), PRINCIPALES, POSTRES), "3 entrantes");
    }

    // CA12.2
    @Test
    void rechazaSiNoHayTresPrincipales() {
        assertRechazado(new ConfigurarMenuRequest(FECHA, ENTRANTES, List.of(4L, 5L, 6L, 9L), POSTRES), "3 principales");
    }

    // CA12.3
    @Test
    void rechazaSiNoHayDosPostres() {
        assertRechazado(new ConfigurarMenuRequest(FECHA, ENTRANTES, PRINCIPALES, List.of(7L)), "2 postres");
    }

    @Test
    void rechazaListaNula() {
        assertRechazado(new ConfigurarMenuRequest(FECHA, ENTRANTES, null, POSTRES), "3 principales");
    }

    @Test
    void rechazaPlatoSinIdentificador() {
        assertRechazado(new ConfigurarMenuRequest(FECHA, Arrays.asList(1L, null, 3L), PRINCIPALES, POSTRES), "sin identificar");
    }

    @Test
    void rechazaPlatoRepetidoEnUnGrupo() {
        assertRechazado(new ConfigurarMenuRequest(FECHA, ENTRANTES, PRINCIPALES, List.of(7L, 7L)), "repetir");
    }

    private void assertRechazado(ConfigurarMenuRequest request, String motivo) {
        assertThatThrownBy(() -> menuService.configurarMenu(request))
                .isInstanceOf(DatosMenuNoValidosException.class)
                .hasMessageContaining(motivo);
        verifyNoInteractions(menuRepository, opcionMenuRepository, platoRepository);
    }
}
