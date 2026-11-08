package com.saborhimalaya.controller;

import org.springframework.context.annotation.Import;
import com.saborhimalaya.config.SecurityConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.UUID;

import com.saborhimalaya.config.SecurityConfig;
import com.saborhimalaya.dto.CrearPlatoRequest;
import com.saborhimalaya.dto.PedidoRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.saborhimalaya.entity.Categoria;
import com.saborhimalaya.entity.Pedido;
import com.saborhimalaya.entity.Plato;
import com.saborhimalaya.entity.Usuario;
import com.saborhimalaya.service.PedidoService;

@WebMvcTest(PedidoController.class)
@Import(SecurityConfig.class)
class PedidoControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private PedidoService pedidoService;

        private Usuario usuario;
        private Plato plato;
        private Pedido pedido;

        @BeforeEach
        void preparaDatos() {
                usuario = new Usuario(
                                "Ana", "Calle Nepal 1", "ana",
                                "ana@example.com", "secreto", true);

                plato = new Plato(
                                "pastel de calabaza",
                                "Ejemplo",
                                new BigDecimal("8.50"),
                                Categoria.POSTRES,
                                true);

                pedido = new Pedido();
                pedido.setIdPedido(1L);
                pedido.setImporteTotal(new BigDecimal("17.00"));
                pedido.setEstado(Pedido.EN_PREPARACION);
        }

        @Test
        void creaPedidoGuardaLosDatosYDevuelve201ConLocation() throws Exception {

                when(pedidoService.AnadirPedido(any(PedidoRequest.class), eq("ana")))
                                .thenReturn(pedido);

                String contenido = """
                                {"platos":[{"idPlato":1,"cantidad":2}]}
                                """;

                String location = mockMvc.perform(post("/api/pedidos")
                                .with(user(usuario.getLogin()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(contenido))
                                .andExpect(status().isCreated())
                                .andReturn().getResponse().getHeader("Location");

                assertThat(location).endsWith("/api/pedidos/1");

                verify(pedidoService).AnadirPedido(
                                any(PedidoRequest.class), eq("ana"));
        }

        @Test
        void exigeAutenticacionParaCrearUnPedido() throws Exception {

                mockMvc.perform(post("/api/pedidos")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"platos\":[]}"))
                                .andExpect(status().isForbidden());
        }
}
