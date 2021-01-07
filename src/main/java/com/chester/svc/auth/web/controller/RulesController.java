package com.chester.svc.auth.web.controller;

import com.chester.svc.auth.access.mongodb.AuthRuleDao;
import com.chester.svc.auth.client.annotation.Roles;
import com.chester.svc.auth.client.core.UserTokenHolder;
import com.chester.svc.auth.client.model.AuthRule;
import com.chester.svc.auth.web.model.req.ReqRule;
import com.chester.svc.auth.web.model.req.ReqRuleMultiple;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/sys/rules")
public class RulesController {
    @Resource
    private AuthRuleDao authRuleDao;

    @PutMapping
    @Roles(value = "admin", remark = "设置路由权限", modify = false)
    public void updateRule(@RequestBody ReqRule reqRule) {
        authRuleDao.updateRule(reqRule, UserTokenHolder.getUserId());
    }

    @PutMapping("/push")
    @Roles(value = "admin", remark = "批量添加路由权限", modify = false)
    public void pushRule(@RequestBody ReqRuleMultiple ruleMultiple) {
        authRuleDao.pushRule(ruleMultiple, UserTokenHolder.getUserId());
    }

    @PutMapping("/pull")
    @Roles(value = "admin", remark = "批量删除路由权限", modify = false)
    public void pullRule(@RequestBody ReqRuleMultiple ruleMultiple) {
        authRuleDao.pullRule(ruleMultiple, UserTokenHolder.getUserId());
    }

    @GetMapping
    @Roles(value = "admin", remark = "获取可编辑路由列表", modify = false)
    public List<AuthRule> getRules() {
        return authRuleDao.find();
    }

    @GetMapping("/mine")
    @Roles(value = "authed", remark = "获取用户的路由列表", modify = false)
    public List<AuthRule> getMyRules() {
        List<String> roles = UserTokenHolder.getRoles();
        return authRuleDao.find(roles);
    }

}
