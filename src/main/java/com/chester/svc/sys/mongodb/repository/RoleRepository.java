package com.chester.svc.sys.mongodb.repository;

import com.chester.cloud.support.mongodb.AccessUtils;
import com.chester.data.mongo.MongoInt64IdGenerator;
import com.chester.svc.sys.mongodb.config.Constant;
import com.chester.svc.sys.mongodb.config.MongoCollections;
import com.chester.svc.sys.mongodb.model.Role;
import com.chester.svc.sys.web.model.res.ResRole;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RoleRepository {
    @Resource
    private MongoDatabase db;
    private MongoCollection<Role> coll;
    @Resource
    private MongoInt64IdGenerator roleGenerator;
    @Resource
    private UserRepository userRepository;

    @PostConstruct
    public void afterPropertiesSet() {
        this.coll = db.getCollection(MongoCollections.role, Role.class);
        Role role = this.coll.find().first();
        if (role == null) {
            role = new Role();
            role.setName("admin");
            role.setRemark("管理员");
            role.setModify(false);
            addRole(role, null);
            role = new Role();
            role.setName("authed");
            role.setRemark("任何登录用户");
            role.setModify(false);
            addRole(role, null);
            role = new Role();
            role.setName("operator");
            role.setRemark("操作员");
            role.setModify(false);
            addRole(role, null);
        }
    }

    public void addRole(Role role, Long createdBy) {
        AccessUtils.prepareEntityBeforeInstall(role, createdBy, userRepository.getUserName(createdBy));
        role.setRoleId(roleGenerator.generate());
        this.coll.insertOne(role);
    }

    public void deleteRole(Long roleId, Long updatedBy) {
        Bson filter = Filters.and(Filters.eq(Constant._id, roleId), Filters.eq(Constant.modify, true));
        this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                Updates.set(Constant.isDeleted, true)
        ));
    }

    public List<ResRole> findRoles() {
        return this.coll.find(ResRole.class).into(new ArrayList<>());
    }
}
