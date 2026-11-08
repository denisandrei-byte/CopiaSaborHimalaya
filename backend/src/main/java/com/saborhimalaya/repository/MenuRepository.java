package com.saborhimalaya.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saborhimalaya.entity.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    Optional<Menu> findByFecha(LocalDate fecha);
}
