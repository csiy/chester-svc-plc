package com.chester.svc.sys.mongodb.repository;

import com.chester.svc.support.util.RegularUtils;
import com.chester.svc.sys.mongodb.config.Constant;
import com.chester.svc.sys.mongodb.config.MongoCollections;
import com.chester.svc.sys.mongodb.model.User;
import com.chester.svc.sys.web.model.req.ReqQueryUser;
import com.chester.svc.sys.web.model.req.ReqUpdateUserInfo;
import com.chester.svc.sys.web.model.res.ResUser;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Slf4j
@Repository
public class UserRepository {
    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 添加用户
     *
     * @param user
     */
    public User add(User user) {
        Assert.isTrue(RegularUtils.isMobile(user.getPhone()), "请输入正确的手机号");
        Assert.notNull(user.getPassword(), "请设置密码");
        Assert.isNull(getUser(user.getPhone()), "手机号已被注册");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (!Constant.female.equals(user.getSex())) {
            user.setSex(Constant.man);
        }
        return user;
    }

    public void update(User user, Long updatedBy) {
        Assert.isTrue(RegularUtils.isMobile(user.getPhone()), "请输入正确的手机号");
        User _user = getUser(user.getPhone());
        if (_user != null) {
            Assert.isTrue(user.getUserId().equals(_user.getUserId()), "手机号已被注册");
        }
    }

    /**
     * 获取用户信息
     *
     * @param phone
     * @return
     */
    public User getUser(String phone) {
        return null;
    }

    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    public ResUser getUser(Long userId) {
        return null;
    }

    public String getUserName(Long userId) {
        if (userId == null) {
            return null;
        }
        return null;
    }

    /**
     * 禁用或启用
     *
     * @param userId
     * @param updatedBy
     */
    public void switchDisabled(Long userId, Long updatedBy) {

    }

    /**
     * 删除用户
     *
     * @param userId
     * @param updatedBy
     */
    public void deleteUser(Long userId, Long updatedBy) {

    }

    /**
     * 更新用户信息
     *
     * @param updateUserInfo
     * @param updatedBy
     */
    public void updateUserInfo(ReqUpdateUserInfo updateUserInfo, Long updatedBy) {

    }

    /**
     * 重置密码
     *
     * @param userId
     * @param updatedBy
     */
    public String reSetPassword(Long userId, Long updatedBy) {
        return null;
    }

    /**
     * 用户分页查询
     *
     * @param query
     * @param pagination
     * @return
     */
    public PageResult<ResUser> find(ReqQueryUser query, Pagination pagination) {
        return null;
    }

    /**
     * 随机获取一名工人
     * @return
     */
    public ResUser randomUser(){
        return null;
    }
}
