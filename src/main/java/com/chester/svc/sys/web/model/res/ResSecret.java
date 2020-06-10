package com.chester.svc.sys.web.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("返回密钥")
public class ResSecret {
    @ApiModelProperty("密钥")
    private String secret;
}
