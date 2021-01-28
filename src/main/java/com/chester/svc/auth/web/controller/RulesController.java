package com.chester.svc.auth.web.controller;

import com.chester.auth.client.annotation.Roles;
import com.chester.auth.client.core.UserTokenHolder;
import com.chester.auth.client.model.AuthRule;
import com.chester.svc.auth.access.mongodb.AuthRuleDao;
import com.chester.svc.auth.mq.MqProducer;
import com.chester.svc.auth.web.model.req.ReqRule;
import com.chester.svc.auth.web.model.req.ReqRuleMultiple;
import com.chester.svc.auth.web.model.res.ResRule;
import com.chester.util.coll.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;

@ApiIgnore
@RestController
@Slf4j
@RequestMapping("/sys/rules")
public class RulesController {
    @Resource
    private AuthRuleDao authRuleDao;
    @Resource
    private MqProducer mqProducer;

    @PutMapping
    @Roles(value = "admin", remark = "设置路由权限", modify = false)
    public void updateRule(@RequestBody ReqRule reqRule) {
        AuthRule authRule = authRuleDao.updateRule(reqRule, UserTokenHolder.getUserId());
        mqProducer.autRuleChange(authRule);
    }

    @PutMapping("/push")
    @Roles(value = "admin", remark = "批量添加路由权限", modify = false)
    public void pushRule(@RequestBody ReqRuleMultiple ruleMultiple) {
        List<AuthRule> rules = authRuleDao.pushRule(ruleMultiple, UserTokenHolder.getUserId());
        Lists.each(rules, v -> {
            mqProducer.autRuleChange(v);
        });
    }

    @PutMapping("/pull")
    @Roles(value = "admin", remark = "批量删除路由权限", modify = false)
    public void pullRule(@RequestBody ReqRuleMultiple ruleMultiple) {
        List<AuthRule> rules = authRuleDao.pullRule(ruleMultiple, UserTokenHolder.getUserId());
        Lists.each(rules, v -> {
            mqProducer.autRuleChange(v);
        });
    }

    @GetMapping
    @Roles(value = "admin", remark = "获取可编辑路由列表", modify = false)
    public List<ResRule> getRules() {
        return authRuleDao.find();
    }

    @GetMapping("/mine")
    @Roles(value = "authed", remark = "获取用户的路由列表", modify = false)
    public List<ResRule> getMyRules() {
        List<String> roles = UserTokenHolder.getRoles();
        return authRuleDao.find(roles);
    }

}
