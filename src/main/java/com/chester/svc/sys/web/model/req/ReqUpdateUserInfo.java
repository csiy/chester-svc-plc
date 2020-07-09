package com.chester.svc.sys.web.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("修改用户信息")
public class ReqUpdateUserInfo {
    @ApiModelProperty("头像")
    private String photo;//头像
    @ApiModelProperty("性别")
    private String sex;//性别
    @ApiModelProperty("生日")
    private Date birthday;
}
