package com.chester.svc.auth.access.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.List;

@Data
public class Account {

    @BsonProperty("_id")
    @ApiModelProperty("用户ID")
    private Long userId;
    @ApiModelProperty("用户名")
    private String name;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("是否禁用")
    private Boolean isDisabled;
    @ApiModelProperty("拥有角色ID")
    private List<String> roles;
}
