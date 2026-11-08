package com.saborhimalaya.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.saborhimalaya.entity.Menu;
import com.saborhimalaya.entity.OpcionMenu;

public interface OpcionMenuRepository extends JpaRepository<OpcionMenu, Long> {

    List<OpcionMenu> findByMenuOrderByTipoOpcionAscOrdenAsc(Menu menu);

    void deleteByMenu(Menu menu);
}
