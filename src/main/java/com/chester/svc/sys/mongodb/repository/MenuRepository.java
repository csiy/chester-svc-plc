package com.chester.svc.sys.mongodb.repository;

import com.chester.svc.sys.mongodb.model.Menu;
import com.chester.svc.sys.web.model.req.ReqMenu;
import com.chester.svc.sys.web.model.req.ReqMenuUpdate;
import com.chester.svc.sys.web.model.res.ResMenu;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

@Repository

@Data
@Slf4j
public class MenuRepository {
    @Resource
    private UserRepository userRepository;

    @PostConstruct
    public void afterPropertiesSet() {

    }

    public void addMenu(Menu menu, Long createdBy) {

    }

    public void updateMenu(ReqMenuUpdate menu, Long updatedBy) {

    }

    public void pullMenu(ReqMenu menu, Long updatedBy) {

    }

    public void pushMenu(ReqMenu menu, Long updatedBy) {

    }

    public List<ResMenu> findMenu() {
        return null;
    }

    public List<ResMenu> findMenu(List<String> roles) {
        return null;
    }

}
