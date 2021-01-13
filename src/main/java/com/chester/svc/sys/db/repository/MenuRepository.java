package com.chester.svc.sys.db.repository;

import com.chester.svc.sys.db.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long> {

}
