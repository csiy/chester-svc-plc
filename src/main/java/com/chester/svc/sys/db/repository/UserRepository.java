package com.chester.svc.sys.db.repository;

import com.chester.svc.sys.db.model.User;
import com.chester.svc.sys.web.model.req.ReqQueryUser;
import com.chester.svc.sys.web.model.req.ReqUpdateUserInfo;
import com.chester.svc.sys.web.model.res.ResUser;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User,Long> {

    void update(User user, Long updatedBy);

    @Modifying
    @Query("update User u set u.roles = ?1 where u.userId = ?2")
    int modifyRoles(String  roles, Long id);

    @Modifying
    @Query("update User u set u.name = ?1 where u.userId = ?2")
    int modifyName(String  name, Long id);

    @Modifying
    @Query("update User u set u.phone = ?1 where u.userId = ?2")
    int modifyPhone(String  phone, Long id);

    /**
     * 获取用户信息
     */
    ResUser getUser(Long userId);

    /**
     * 禁用或启用
     */
    void switchDisabled(Long userId, Long updatedBy);

    /**
     * 删除用户
     */
    void deleteUser(Long userId, Long updatedBy);

    /**
     * 更新用户信息
     */
    void updateUserInfo(ReqUpdateUserInfo updateUserInfo, Long updatedBy);

    /**
     * 重置密码
     */
    String reSetPassword(Long userId, Long updatedBy);

    /**
     * 用户分页查询
     */
    PageResult<ResUser> find(ReqQueryUser query, Pagination pagination);

}
