package com.chester.svc.sys.db.repository;

import com.chester.svc.support.util.RegularUtils;
import com.chester.svc.sys.db.model.User;
import com.chester.svc.sys.web.model.req.ReqQueryUser;
import com.chester.svc.sys.web.model.req.ReqUpdateUserInfo;
import com.chester.svc.sys.web.model.res.ResUser;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import javax.annotation.Resource;


@Slf4j
@Repository
public class UserRepository {
    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 添加用户
     */
    public User add(User user) {
        Assert.isTrue(RegularUtils.isMobile(user.getPhone()), "请输入正确的手机号");
        Assert.notNull(user.getPassword(), "请设置密码");
        Assert.isNull(getUser(user.getPhone()), "手机号已被注册");
        user.setPassword(passwordEncoder.encode(user.getPassword()));

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
     */
    public User getUser(String phone) {
        return null;
    }

    /**
     * 获取用户信息
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
     */
    public void switchDisabled(Long userId, Long updatedBy) {

    }

    /**
     * 删除用户
     */
    public void deleteUser(Long userId, Long updatedBy) {

    }

    /**
     * 更新用户信息
     */
    public void updateUserInfo(ReqUpdateUserInfo updateUserInfo, Long updatedBy) {

    }

    /**
     * 重置密码
     */
    public String reSetPassword(Long userId, Long updatedBy) {
        return null;
    }

    /**
     * 用户分页查询
     */
    public PageResult<ResUser> find(ReqQueryUser query, Pagination pagination) {
        return null;
    }

    /**
     * 随机获取一名工人
     */
    public ResUser randomUser(){
        return null;
    }
}
