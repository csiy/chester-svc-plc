package com.chester.svc.auth.db.repository;

import com.chester.svc.auth.access.entity.Account;
import com.chester.svc.auth.db.model.AuthRule;
import com.chester.svc.auth.web.model.req.ReqRule;
import com.chester.svc.auth.web.model.req.ReqRuleMultiple;
import com.chester.svc.sys.db.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
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

    public Account getAccountByPhoneOrId(String username) {
        Account account = new Account();//phone
        if (account == null) {
            try {
                Long userId = Long.parseLong(username);
            } catch (Exception e) {
                throw new IllegalArgumentException("请输入正确的账号");
            }
        }
        if (account == null) {
            throw new IllegalArgumentException("无效的账号");
        }
        return account;
    }

}
