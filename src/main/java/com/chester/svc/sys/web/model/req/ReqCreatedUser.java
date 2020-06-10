package com.chester.svc.sys.web.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("创建账号")
public class ReqCreatedUser {
    @ApiModelProperty("手机号")
    private String phone;
    @ApiModelProperty("姓名")
    private String name;
    @ApiModelProperty("密码")
    private String password;
    @ApiModelProperty("角色")
    private String role;
    @ApiModelProperty("商品Id,仅商户角色有用")
    private Long shopId;
}
