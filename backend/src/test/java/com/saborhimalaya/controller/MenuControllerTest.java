package com.saborhimalaya.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.saborhimalaya.dto.PlatoDto;
import com.saborhimalaya.entity.Categoria;
import com.saborhimalaya.entity.Menu;
import com.saborhimalaya.exception.DatosMenuNoValidosException;
import com.saborhimalaya.exception.PlatoNoEncontradoException;
import com.saborhimalaya.service.MenuService;

// Tests de US12 y US13: endpoints POST /api/menus y GET /api/menus/platos
@WebMvcTest(MenuController.class)
class MenuControllerTest {

    private static final String MENU_COMPLETO = """
            {"fecha": "2026-11-05", "entrantes": [1, 2, 3], "principales": [4, 5, 6], "postres": [7, 8]}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MenuService menuService;

    @Test
    void configuraMenuYDevuelve201() throws Exception {
        Menu menu = mock(Menu.class);
        when(menu.getIdMenu()).thenReturn(10L);
        when(menuService.configurarMenu(any())).thenReturn(menu);

        mockMvc.perform(post("/api/menus").with(user("chef")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(MENU_COMPLETO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/menus/10"));
    }

    // CA12.1-CA12.3: un menú incompleto no llega al servicio
    @Test
    void rechazaMenuIncompletoCon400() throws Exception {
        String sinTercerEntrante = """
                {"fecha": "2026-11-05", "entrantes": [1, 2], "principales": [4, 5, 6], "postres": [7, 8]}
                """;

        mockMvc.perform(post("/api/menus").with(user("chef")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(sinTercerEntrante))
                .andExpect(status().isBadRequest());

        verify(menuService, never()).configurarMenu(any());
    }

    @Test
    void devuelve404SiUnPlatoNoExiste() throws Exception {
        when(menuService.configurarMenu(any())).thenThrow(new PlatoNoEncontradoException(7L));

        mockMvc.perform(post("/api/menus").with(user("chef")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(MENU_COMPLETO))
                .andExpect(status().isNotFound());
    }

    // CA13.1-CA13.3: un plato de una categoría que no corresponde devuelve 400
    @Test
    void devuelve400SiUnPlatoNoEsDelTipoDeOpcion() throws Exception {
        when(menuService.configurarMenu(any()))
                .thenThrow(new DatosMenuNoValidosException("El plato Lassi (Bebidas) no se puede elegir como principal"));

        mockMvc.perform(post("/api/menus").with(user("chef")).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(MENU_COMPLETO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("El plato Lassi (Bebidas) no se puede elegir como principal"));
    }

    // US13: listado de platos para el menú, filtrado por tipo y origen
    @Test
    void listaPlatosParaElMenu() throws Exception {
        PlatoDto fueraDeCarta = new PlatoDto(11L, "Momos de cordero", "Momos rellenos de cordero",
                new BigDecimal("10.50"), Categoria.ESPECIALIDADES, false);
        when(menuService.listarPlatosParaMenu("Principal", "fuera")).thenReturn(List.of(fueraDeCarta));

        mockMvc.perform(get("/api/menus/platos").param("tipo", "Principal").param("origen", "fuera")
                .with(user("chef")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].idPlato").value(11))
                .andExpect(jsonPath("$[0].nombre").value("Momos de cordero"))
                .andExpect(jsonPath("$[0].descripcion").value("Momos rellenos de cordero"))
                .andExpect(jsonPath("$[0].precio").value(10.50))
                .andExpect(jsonPath("$[0].categoria").value("Especialidades"))
                .andExpect(jsonPath("$[0].enCarta").value(false));
    }

    @Test
    void listaTodosLosPlatosSinFiltros() throws Exception {
        when(menuService.listarPlatosParaMenu(isNull(), isNull())).thenReturn(List.of());

        mockMvc.perform(get("/api/menus/platos").with(user("chef")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void devuelve400SiElFiltroNoEsValido() throws Exception {
        when(menuService.listarPlatosParaMenu("Bebida", null))
                .thenThrow(new DatosMenuNoValidosException("Tipo de opción no válido: Bebida"));

        mockMvc.perform(get("/api/menus/platos").param("tipo", "Bebida").with(user("chef")))
                .andExpect(status().isBadRequest());
    }
}
