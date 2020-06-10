package com.chester.svc.sys.mongodb.model;

import com.chester.cloud.support.model.BaseEntity;
import com.chester.data.mongo.annotations._id;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 用户数据模型
 */
@Data
public class User extends BaseEntity {
    @_id
    private Long userId;//用户ID
    private String name;//用户名
    private String sex;//性别
    private Date birthday;//生日
    private String photo;//头像
    private String phone;//手机号
    private String password;//密码
    private List<String> roles;//角色列表
}
