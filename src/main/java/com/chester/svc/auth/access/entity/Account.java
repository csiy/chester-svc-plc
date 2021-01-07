package com.chester.svc.auth.access.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class Account {

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
