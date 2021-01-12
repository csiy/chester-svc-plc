package com.chester.svc.sys.db.model;

import com.chester.svc.support.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 用户数据模型
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "plc_user")
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;//用户ID
    private String key;//账号
    private String name;//用户名
    private String sex;//性别
    private Date birthday;//生日
    private String photo;//头像
    private String phone;//手机号
    private String password;//密码
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;//角色列表
    private Boolean isDisabled = false;
}
