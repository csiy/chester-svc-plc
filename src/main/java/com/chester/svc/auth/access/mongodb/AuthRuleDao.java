package com.chester.svc.auth.access.mongodb;

import com.chester.svc.auth.client.model.AuthRule;
import com.chester.svc.auth.web.model.req.ReqRule;
import com.chester.svc.auth.web.model.req.ReqRuleMultiple;
import com.chester.svc.auth.web.model.res.ResRule;
import com.chester.svc.sys.mongodb.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class AuthRuleDao {

    private static final String collectionName = "auth.rules";
    @Resource
    private UserRepository userRepository;


    public AuthRule getRule(AuthRule authRule) {

        return null;
    }


    /**
     * 修改路由权限
     *
     * @param reqRule
     * @param updatedBy
     * @return
     */
    public void updateRule(ReqRule reqRule, Long updatedBy) {
    }

    public void pullRule(ReqRuleMultiple ruleMultiple, Long updatedBy) {

    }

    public void pushRule(ReqRuleMultiple ruleMultiple, Long updatedBy) {

    }

    public List<AuthRule> find() {
        return null;
    }

    public List<AuthRule> find(List<String> roles) {
        return null;
    }

}
