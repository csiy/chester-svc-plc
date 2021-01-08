package com.chester.svc.sys.db.repository;

import com.chester.svc.sys.db.model.User;
import com.chester.svc.sys.web.model.req.ReqQueryUser;
import com.chester.svc.sys.web.model.res.ResUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface UserRepository extends JpaRepository<User,Long> {

    @Modifying
    @Query("update User u set u.roles = ?1 where u.userId = ?2")
    void modifyRoles(String  roles, Long id);

    @Modifying
    @Query("update User u set u.name = ?1 where u.userId = ?2")
    void modifyName(String  name, Long id);

    @Modifying
    @Query("update User u set u.phone = ?1 where u.userId = ?2")
    void modifyPhone(String  phone, Long id);

    @Modifying
    @Query("update User u set u.password = ?1 where u.userId = ?2")
    void modifyPassword(String  password, Long id);

    @Modifying
    @Query("update User u set u.sex = ?1 where u.userId = ?2")
    void modifySex(String  sex, Long id);

    @Modifying
    @Query("update User u set u.birthday = ?1 where u.userId = ?2")
    void modifyBirthday(Date birthday, Long id);

    @Modifying
    @Query("update User u set u.photo = ?1 where u.userId = ?2")
    void modifyPhoto(String photo, Long id);

    @Modifying
    @Query("update User u set u.isDisabled = ?1 where u.userId = ?2")
    void modifyDisabled(Boolean isDisabled ,Long id);

    /**
     * 用户分页查询
     */
    @Query("select u from User u where if(?1 !='',u.name = ?1,1=1) or u.phone = ?2 and u.roles like CONCAT(?3,'%')")
    Page<ResUser> find(String name,String phone,String role, Pageable pageable);

}
