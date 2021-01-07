package com.chester.svc.auth.access.mongodb;

import com.chester.svc.auth.access.entity.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class AccountDao {

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
