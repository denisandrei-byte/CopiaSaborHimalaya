package com.saborhimalaya.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.saborhimalaya.dto.PlatoDto;
import com.saborhimalaya.entity.Categoria;
import com.saborhimalaya.service.PlatoService;

// Tests del endpoint POST /api/platos.
@WebMvcTest(PlatoController.class)
class PlatoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlatoService platoService;

    @Test
    void creaPlatoYDevuelve201() throws Exception {
        PlatoDto plato = new PlatoDto(1L, "Momos", "Momos de verduras", new BigDecimal("7.50"), Categoria.ENTRANTES, true);
        when(platoService.anadirPlatoALaCarta(any())).thenReturn(plato);

        mockMvc.perform(post("/api/platos").with(user("admin")).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("""
                {"nombre":"Momos","descripcion":"Momos de verduras","precio":7.50,"categoria":"Entrantes"}
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idPlato").value(1))
                .andExpect(jsonPath("$.nombre").value("Momos"))
                .andExpect(jsonPath("$.categoria").value("Entrantes"));
    }

    @Test
    void rechazaPrecioNegativoCon400() throws Exception {
        mockMvc.perform(post("/api/platos").with(user("admin")).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("""
                {"nombre":"Momos","descripcion":"Momos de verduras","precio":-1.00,"categoria":"Entrantes"}
                """))
                .andExpect(status().isBadRequest());

        verify(platoService, never()).anadirPlatoALaCarta(any());
    }
}
