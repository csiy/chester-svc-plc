package com.chester.svc.sys.web.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel("用户信息")
public class ResUser {
    @ApiModelProperty("用户ID")
    private Long userId;
    @ApiModelProperty("用户名")
    private String name;
    @ApiModelProperty("性别")
    private String sex;
    @ApiModelProperty("生日")
    private Date birthday;
    @ApiModelProperty("头像")
    private String photo;
    @ApiModelProperty("手机号")
    private String phone;
    @ApiModelProperty("角色列表")
    private List<String> roles;
    @ApiModelProperty("是否禁用")
    private Boolean isDisabled;
}
