package com.chester.svc.sys.web.controller;

import com.chester.svc.sys.annotation.Roles;
import com.chester.svc.sys.db.model.Rule;
import com.chester.svc.sys.db.repository.AuthRuleDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/sys/rules")
public class RulesController {
    @Resource
    private AuthRuleDao authRuleDao;

    @GetMapping
    @Roles(value = "admin", remark = "获取可编辑路由列表", modify = false)
    public List<Rule> getRules() {
        return authRuleDao.findAll();
    }

}
