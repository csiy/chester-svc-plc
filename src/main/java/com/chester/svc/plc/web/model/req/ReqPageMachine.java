package com.chester.svc.plc.web.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("机器")
public class ReqPageMachine {
    @ApiModelProperty("地址")
    private String address;
}
