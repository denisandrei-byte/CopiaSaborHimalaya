package com.saborhimalaya.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import jakarta.persistence.EntityManager;

import com.saborhimalaya.dto.CrearPlatoRequest;
import com.saborhimalaya.dto.PlatoDto;
import com.saborhimalaya.entity.Categoria;
import com.saborhimalaya.entity.Plato;
import com.saborhimalaya.repository.PlatoRepository;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class PlatoServiceTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    private PlatoRepository platoRepository;

    @Autowired
    private EntityManager entityManager;

    private PlatoService platoService;

    @BeforeEach
    void prepararTest() {
        // El servicio utiliza el repositorio real conectado al PostgreSQL temporal.
        platoService = new PlatoService(platoRepository);
    }

    @Test
    void devuelveUnaListaVaciaCuandoNoHayPlatosEnCarta() {
        // Cada test empieza sin los datos de los otros tests.
        List<PlatoDto> carta = platoService.obtenerPlatosDeLaCarta();

        // Comprobar el resultado.
        assertThat(carta).isEmpty();
    }

    @Test
    void devuelveLosDatosDelPlatoAlConsultarLaCarta() {
        Plato plato = new Plato("Momos", "Momos de verduras", new BigDecimal("7.50"), Categoria.ENTRANTES, true);
        Plato platoFueraDeCarta = new Plato("Curry oculto", "Curry no disponible", new BigDecimal("10.00"),
                Categoria.PRINCIPALES, false);
        platoRepository.saveAndFlush(plato);
        platoRepository.saveAndFlush(platoFueraDeCarta);
        entityManager.clear();

        List<PlatoDto> carta = platoService.obtenerPlatosDeLaCarta();

        assertThat(carta).hasSize(1);
        PlatoDto resultado = carta.get(0);
        assertThat(resultado.nombre()).isEqualTo("Momos");
        assertThat(resultado.descripcion()).isEqualTo("Momos de verduras");
        assertThat(resultado.precio()).isEqualByComparingTo("7.50");
        assertThat(resultado.categoria()).isEqualTo(Categoria.ENTRANTES);
        assertThat(resultado.enCarta()).isTrue();
    }

    @Test
    void guardaElNuevoPlatoComoDisponibleEnCarta() {
        CrearPlatoRequest request = new CrearPlatoRequest("Curry", "Curry de verduras", new BigDecimal("12.00"),
                Categoria.PRINCIPALES);

        PlatoDto resultado = platoService.anadirPlatoALaCarta(request);

        // Enviamos los cambios a PostgreSQL y vaciamos la caché de JPA para leer de la
        // base de datos.
        platoRepository.flush();
        entityManager.clear();
        assertThat(resultado.idPlato()).isNotNull();
        Plato platoGuardado = platoRepository.findById(resultado.idPlato()).orElseThrow();
        assertThat(platoGuardado.getNombre()).isEqualTo("Curry");
        assertThat(platoGuardado.getDescripcion()).isEqualTo("Curry de verduras");
        assertThat(platoGuardado.getPrecio()).isEqualByComparingTo("12.00");
        assertThat(platoGuardado.getCategoria()).isEqualTo(Categoria.PRINCIPALES);
        assertThat(platoGuardado.isEnCarta()).isTrue();
        assertThat(resultado.nombre()).isEqualTo("Curry");
        assertThat(resultado.enCarta()).isTrue();
    }
}
