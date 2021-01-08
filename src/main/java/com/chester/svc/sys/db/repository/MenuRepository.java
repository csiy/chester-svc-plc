package com.chester.svc.sys.db.repository;

import com.chester.svc.sys.db.model.Menu;
import com.chester.svc.sys.web.model.req.ReqMenu;
import com.chester.svc.sys.web.model.req.ReqMenuUpdate;
import com.chester.svc.sys.web.model.res.ResMenu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    void addMenu(Menu menu, Long createdBy);

    void updateMenu(ReqMenuUpdate menu, Long updatedBy);

    void pullMenu(ReqMenu menu, Long updatedBy);

    void pushMenu(ReqMenu menu, Long updatedBy);

    List<ResMenu> findMenu();

    List<ResMenu> findMenu(List<String> roles);

}
