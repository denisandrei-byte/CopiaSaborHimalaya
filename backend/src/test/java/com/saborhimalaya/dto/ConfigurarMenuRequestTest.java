package com.saborhimalaya.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

// Tests de US12: reglas de 3 entrantes, 3 principales y 2 postres
class ConfigurarMenuRequestTest {

    private static final LocalDate FECHA = LocalDate.of(2026, 11, 5);

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void crearValidador() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void cerrarValidador() {
        factory.close();
    }

    @Test
    void aceptaMenuCompleto() {
        ConfigurarMenuRequest request = new ConfigurarMenuRequest(FECHA, List.of(1L, 2L, 3L), List.of(4L, 5L, 6L), List.of(7L, 8L));

        assertThat(validator.validate(request)).isEmpty();
    }

    // CA12.1
    @Test
    void rechazaSiNoHayTresEntrantes() {
        ConfigurarMenuRequest request = new ConfigurarMenuRequest(FECHA, List.of(1L, 2L), List.of(4L, 5L, 6L), List.of(7L, 8L));

        assertThat(camposConError(validator.validate(request))).containsExactly("entrantes");
    }

    // CA12.2
    @Test
    void rechazaSiNoHayTresPrincipales() {
        ConfigurarMenuRequest request = new ConfigurarMenuRequest(FECHA, List.of(1L, 2L, 3L), List.of(4L, 5L, 6L, 9L), List.of(7L, 8L));

        assertThat(camposConError(validator.validate(request))).containsExactly("principales");
    }

    // CA12.3
    @Test
    void rechazaSiNoHayDosPostres() {
        ConfigurarMenuRequest request = new ConfigurarMenuRequest(FECHA, List.of(1L, 2L, 3L), List.of(4L, 5L, 6L), List.of(7L));

        assertThat(camposConError(validator.validate(request))).containsExactly("postres");
    }

    @Test
    void rechazaSinFecha() {
        ConfigurarMenuRequest request = new ConfigurarMenuRequest(null, List.of(1L, 2L, 3L), List.of(4L, 5L, 6L), List.of(7L, 8L));

        assertThat(camposConError(validator.validate(request))).containsExactly("fecha");
    }

    @Test
    void rechazaPlatoSinIdentificador() {
        ConfigurarMenuRequest request = new ConfigurarMenuRequest(FECHA, Arrays.asList(1L, null, 3L), List.of(4L, 5L, 6L), List.of(7L, 8L));

        assertThat(validator.validate(request)).isNotEmpty();
    }

    private List<String> camposConError(Set<ConstraintViolation<ConfigurarMenuRequest>> errores) {
        return errores.stream().map(e -> e.getPropertyPath().toString()).toList();
    }
}
