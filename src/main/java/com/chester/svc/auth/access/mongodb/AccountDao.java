package com.chester.svc.auth.access.mongodb;

import com.chester.svc.auth.access.entity.Account;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Repository;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.include;

@Slf4j
@Repository
public class AccountDao {

    private MongoCollection<Account> coll;

    public AccountDao(MongoDatabase db) {
        this.coll = db.getCollection("user.user2", Account.class);
    }

    public Account getAccountByPhoneOrId(String username) {
        Bson filter = eq("phone", username);
        Bson projection = include("_id", "password", "name", "password", "isDisabled", "roles");
        Account account = this.coll.find(filter).projection(projection).first();
        if (account == null) {
            try {
                Long userId = Long.parseLong(username);
                filter = eq("_id", userId);
                account = this.coll.find(filter).projection(projection).first();
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
