package com.saborhimalaya.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saborhimalaya.entity.Plato;

public interface PlatoRepository extends JpaRepository<Plato, Long> {

    List<Plato> findByEnCartaTrueOrderByCategoriaAscNombreAsc();

    // US13: todos los platos del restaurante, estén o no en la carta
    List<Plato> findAllByOrderByCategoriaAscNombreAsc();

    Optional<Plato> findByIdPlatoAndEnCarta(Long idPlato, boolean enCarta);
}
