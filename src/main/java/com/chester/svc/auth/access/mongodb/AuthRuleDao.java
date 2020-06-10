package com.chester.svc.auth.access.mongodb;

import com.chester.auth.client.model.AuthRule;
import com.chester.cloud.support.mongodb.AccessUtils;
import com.chester.svc.auth.web.model.req.ReqRule;
import com.chester.svc.auth.web.model.req.ReqRuleMultiple;
import com.chester.svc.auth.web.model.res.ResRule;
import com.chester.svc.sys.mongodb.config.Constant;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class AuthRuleDao {

    private MongoCollection<AuthRule> coll;
    private static final String collectionName = "auth.rules";

    public AuthRuleDao(MongoDatabase db) {
        this.coll = db.getCollection(collectionName, AuthRule.class);
        this.coll.createIndex(Indexes.descending("path","model","type"), new IndexOptions().unique(true));
    }

    /**
     * 添加权限
     *
     * @param authRule
     */
    public Boolean initRule(AuthRule authRule) {
        if(getRule(authRule)==null){
            Bson filter = Filters.and(
                    Filters.eq("path",authRule.getPath()),
                    Filters.eq("model",authRule.getModel()),
                    Filters.eq("type",authRule.getType())
            );
            UpdateResult result = this.coll.updateOne(filter,Updates.combine(
                    Updates.set(Constant.modify,authRule.getModify()),
                    Updates.set(Constant.roles,authRule.getRoles()),
                    Updates.set(Constant.remark,authRule.getRemark())
            ),new UpdateOptions().upsert(true));
            return true;
        }
        return false;
    }

    public AuthRule getRule(AuthRule authRule){
        Bson filter = Filters.and(
                Filters.eq("path",authRule.getPath()),
                Filters.eq("model",authRule.getModel()),
                Filters.eq("type",authRule.getType())
        );
        return this.coll.find(filter).first();
    }

    public AuthRule getRule(ObjectId ruleId){
        return this.coll.find(Filters.eq(Constant._id, ruleId)).first();
    }

    public List<AuthRule> getRules(List<ObjectId> ruleIds){
        return this.coll.find(Filters.in("_id", ruleIds)).into(new ArrayList<>());
    }


    /**
     * 修改路由权限
     * @param reqRule
     * @param updatedBy
     * @return
     */
    public AuthRule updateRule(ReqRule reqRule, Long updatedBy) {
        UpdateResult result = this.coll.updateOne(
                Filters.and(Filters.eq(Constant._id, reqRule.getRuleId()),Filters.eq(Constant.modify,true)),
                AccessUtils.prepareUpdates(updatedBy,
                        Updates.set(Constant.roles, reqRule.getRoles())
                ));
        return this.coll.find(Filters.eq(Constant._id, reqRule.getRuleId())).first();
    }

    public List<AuthRule> pullRule(ReqRuleMultiple ruleMultiple,Long updatedBy){
        Bson filter = Filters.and(Filters.in(Constant._id, ruleMultiple.getRuleIds()),Filters.eq(Constant.modify, true));
        this.coll.updateMany(filter, AccessUtils.prepareUpdates(updatedBy,
                Updates.pull(Constant.roles, ruleMultiple.getRole())
        ));
        return getRules(ruleMultiple.getRuleIds());
    }

    public List<AuthRule> pushRule(ReqRuleMultiple ruleMultiple,Long updatedBy){
        Bson filter = Filters.and(Filters.in(Constant._id, ruleMultiple.getRuleIds()),Filters.eq(Constant.modify, true));
        this.coll.updateMany(filter, AccessUtils.prepareUpdates(updatedBy,
                Updates.addToSet(Constant.roles, ruleMultiple.getRole())
        ));
        return getRules(ruleMultiple.getRuleIds());
    }

    public List<ResRule> find(){
        return this.coll.find(Filters.eq(Constant.modify,true),ResRule.class).into(new ArrayList<>());
    }

    public List<ResRule> find(List<String> roles){
        return this.coll.find(Filters.in(Constant.roles, roles),ResRule.class).into(new ArrayList<>());
    }

}
