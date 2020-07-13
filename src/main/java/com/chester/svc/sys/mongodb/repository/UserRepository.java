package com.chester.svc.sys.mongodb.repository;

import com.chester.cloud.support.mongodb.AccessUtils;
import com.chester.cloud.support.util.RegularUtils;
import com.chester.data.mongo.MongoInt64IdGenerator;
import com.chester.data.mongo.MongoPageQuery;
import com.chester.svc.sys.mongodb.config.Constant;
import com.chester.svc.sys.mongodb.config.MongoCollections;
import com.chester.svc.sys.mongodb.model.User;
import com.chester.svc.sys.web.model.req.ReqQueryUser;
import com.chester.svc.sys.web.model.req.ReqUpdateUserInfo;
import com.chester.svc.sys.web.model.res.ResUser;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import com.google.common.collect.ImmutableList;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import lombok.extern.slf4j.Slf4j;
import org.bson.conversions.Bson;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.sample;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Filters.gte;

@Slf4j
@Repository
public class UserRepository {
    @Resource
    private MongoDatabase db;
    @Resource
    private PasswordEncoder passwordEncoder;
    private MongoCollection<User> coll;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private MongoInt64IdGenerator userGenerator;

    @PostConstruct
    public void afterPropertiesSet() {
        this.coll = db.getCollection(MongoCollections.user, User.class);
        this.coll.createIndex(Indexes.descending(Constant.phone));
        this.coll.createIndex(Indexes.descending(Constant.createdOn));
        User user = this.coll.find().first();
        if (user == null) {
            this.userGenerator.bulkGenerate(99999);
            initRootUser();
        }
    }

    /**
     * 初始化管理员账号
     */
    public void initRootUser() {
        User user = new User();
        user.setBirthday(new Date());
        user.setName("管理员");
        user.setPassword("123456");
        user.setPhone("18888888888");
        user.setRoles(ImmutableList.of("admin"));
        add(user);
    }

    /**
     * 添加用户
     *
     * @param user
     */
    public User add(User user) {
        Assert.isTrue(RegularUtils.isMobile(user.getPhone()), "请输入正确的手机号");
        Assert.notNull(user.getPassword(), "请设置密码");
        Assert.isNull(getUser(user.getPhone()), "手机号已被注册");
        user.setUserId(userGenerator.generate());
        Long createdBy = user.getCreatedBy() == null ? user.getUserId() : user.getCreatedBy();
        AccessUtils.prepareEntityBeforeInstall(user, createdBy, getUserName(createdBy));
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (!Constant.female.equals(user.getSex())) {
            user.setSex(Constant.man);
        }
        this.coll.insertOne(user);
        return user;
    }

    public void update(User user, Long updatedBy) {
        Assert.isTrue(RegularUtils.isMobile(user.getPhone()), "请输入正确的手机号");
        User _user = getUser(user.getPhone());
        if (_user != null) {
            Assert.isTrue(user.getUserId().equals(_user.getUserId()), "手机号已被注册");
        }
        this.coll.updateOne(
                Filters.eq(Constant._id, user.getUserId()),
                AccessUtils.prepareUpdates(updatedBy, getUserName(updatedBy),
                        Updates.set(Constant.name, user.getName()),
                        Updates.set(Constant.phone, user.getPhone()),
                        Updates.set(Constant.roles, user.getRoles())
                )
        );
    }

    /**
     * 获取用户信息
     *
     * @param phone
     * @return
     */
    public User getUser(String phone) {
        Bson filter = Filters.and(Filters.eq(Constant.phone, phone), Filters.eq(Constant.isDeleted, Boolean.FALSE));
        return this.coll.find(filter).first();
    }

    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    public ResUser getUser(Long userId) {
        Bson filter = Filters.and(Filters.eq(Constant._id, userId), Filters.eq(Constant.isDeleted, Boolean.FALSE));
        return this.coll.find(filter, ResUser.class).first();
    }

    public String getUserName(Long userId) {
        if (userId == null) {
            return null;
        }
        String key = "u:n:" + userId;
        String name = stringRedisTemplate.opsForValue().get(key);
        if (name != null) {
            return name;
        }
        Bson filter = Filters.and(Filters.eq(Constant._id, userId), Filters.eq(Constant.isDeleted, Boolean.FALSE));
        ResUser user = this.coll.find(filter, ResUser.class).first();
        if (user != null) {
            stringRedisTemplate.opsForValue().set(key, user.getName(), 30, TimeUnit.DAYS);
            return user.getName();
        } else {
            return null;
        }
    }

    /**
     * 禁用或启用
     *
     * @param userId
     * @param updatedBy
     */
    public void switchDisabled(Long userId, Long updatedBy) {
        Bson filter = Filters.eq(Constant._id, userId);
        Boolean isDisabled = Objects.requireNonNull(this.coll.find(filter).first()).getIsDisabled();
        this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, getUserName(updatedBy),
                Updates.set(Constant.isDisabled, !isDisabled)
        ));
    }

    /**
     * 删除用户
     *
     * @param userId
     * @param updatedBy
     */
    public void deleteUser(Long userId, Long updatedBy) {
        Bson filter = Filters.eq(Constant._id, userId);
        this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, getUserName(updatedBy),
                Updates.set(Constant.isDeleted, true)));
    }

    /**
     * 更新用户信息
     *
     * @param updateUserInfo
     * @param updatedBy
     */
    public void updateUserInfo(ReqUpdateUserInfo updateUserInfo, Long updatedBy) {
        List<Bson> list = new ArrayList<>();
        if (updateUserInfo.getPhoto() != null) {
            list.add(Updates.set(Constant.photo, updateUserInfo.getPhoto()));
        }
        if (updateUserInfo.getSex() != null) {
            list.add(Updates.set(Constant.sex, updateUserInfo.getSex()));
        }
        if (updateUserInfo.getBirthday() != null) {
            list.add(Updates.set(Constant.birthday, updateUserInfo.getBirthday()));
        }
        Bson filter = Filters.eq(Constant._id, updatedBy);
        this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, getUserName(updatedBy), list.toArray(new Bson[]{})));
    }

    /**
     * 重置密码
     *
     * @param userId
     * @param updatedBy
     */
    public String reSetPassword(Long userId, Long updatedBy) {
        Bson filter = Filters.eq(Constant._id, userId);
        String newPassword = (new Random().nextInt(899999) + 100000) + "";
        String pwd = passwordEncoder.encode(newPassword);
        this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, getUserName(updatedBy),
                Updates.set(Constant.password, pwd)
        ));
        return newPassword;
    }

    /**
     * 用户分页查询
     *
     * @param query
     * @param pagination
     * @return
     */
    public PageResult<ResUser> find(ReqQueryUser query, Pagination pagination) {
        Bson sort = Sorts.descending(Constant.createdOn);
        Bson filter = Filters.eq(Constant.isDeleted, Boolean.FALSE);
        if (query.getName() != null) {
            filter = Filters.and(Filters.regex(Constant.name, query.getName()), filter);
        }
        if (query.getPhone() != null) {
            filter = Filters.and(Filters.eq(Constant.phone, query.getPhone()), filter);
        }
        if (query.getRole() != null) {
            filter = Filters.and(Filters.eq(Constant.roles, query.getRole()), filter);
        }
        return MongoPageQuery.builder(coll, ResUser.class).sort(sort).page(pagination).filter(filter).execute();
    }

    /**
     * 随机获取一名工人
     * @return
     */
    public ResUser randomUser(){
        Bson $match = match(and(
                eq(Constant.isDisabled, Boolean.FALSE),
                eq(Constant.roles, "operator"),
                eq(Constant.isDeleted, Boolean.FALSE)));
        Bson $sample = sample(1);
        return coll.aggregate(Arrays.asList($match,$sample),ResUser.class).first();
    }
}
