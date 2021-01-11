package com.chester.svc.auth.web.controller;

import com.chester.svc.auth.client.annotation.Roles;
import com.chester.svc.auth.client.core.UserTokenHolder;
import com.chester.svc.auth.db.model.AuthRule;
import com.chester.svc.auth.db.repository.AuthRuleDao;
import com.chester.svc.auth.web.model.req.ReqRule;
import com.chester.svc.auth.web.model.req.ReqRuleMultiple;
import com.chester.util.coll.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/sys/rules")
public class RulesController {
    @Resource
    private AuthRuleDao authRuleDao;

    @PutMapping
    @Roles(value = "admin", remark = "设置路由权限", modify = false)
    public void updateRule(@RequestBody ReqRule reqRule) {
        AuthRule rule = authRuleDao.getOne(reqRule.getRuleId());
        rule.setRoles(String.join(",",reqRule.getRoles()));
        authRuleDao.save(rule);
    }

    @PutMapping("/push")
    @Roles(value = "admin", remark = "批量添加路由权限", modify = false)
    public void pushRule(@RequestBody ReqRuleMultiple ruleMultiple) {
        List<AuthRule> rules = authRuleDao.findAllById(ruleMultiple.getRuleIds());
        Lists.each(rules,v->{
            v.setRoles(v.getRoles()+","+ruleMultiple.getRole());
        });
        authRuleDao.saveAll(rules);
    }

    @PutMapping("/pull")
    @Roles(value = "admin", remark = "批量删除路由权限", modify = false)
    public void pullRule(@RequestBody ReqRuleMultiple ruleMultiple) {
        List<AuthRule> rules = authRuleDao.findAllById(ruleMultiple.getRuleIds());
        Lists.each(rules,v->{
            String[] roles = v.getRoles().split(",");
            String role = Arrays.stream(roles).filter(j->!j.equals(ruleMultiple.getRole())).collect(Collectors.joining(","));
            v.setRoles(role);
        });
        authRuleDao.saveAll(rules);
    }

    @GetMapping
    @Roles(value = "admin", remark = "获取可编辑路由列表", modify = false)
    public List<AuthRule> getRules() {
        return authRuleDao.findAll();
    }

    @GetMapping("/mine")
    @Roles(value = "authed", remark = "获取用户的路由列表", modify = false)
    public List<AuthRule> getMyRules() {
        List<String> roles = UserTokenHolder.getRoles();
        return Lists.filter(authRuleDao.findAll(),v->{
            for(String role:roles){
                if(v.getRoles().contains(role)){
                    return true;
                }
            }
            return false;
        });
    }

}
