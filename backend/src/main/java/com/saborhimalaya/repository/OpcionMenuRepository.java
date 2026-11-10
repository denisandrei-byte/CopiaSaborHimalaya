package com.saborhimalaya.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saborhimalaya.entity.Menu;
import com.saborhimalaya.entity.OpcionMenu;
import com.saborhimalaya.entity.Plato;

public interface OpcionMenuRepository extends JpaRepository<OpcionMenu, Long> {

    List<OpcionMenu> findByMenuOrderByTipoOpcionAscOrdenAsc(Menu menu);

    void deleteByMenu(Menu menu);

    Optional<OpcionMenu> findByMenuAndPlato(Menu menu, Plato plato);
}
