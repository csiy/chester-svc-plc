package com.chester.svc.plc.web.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("物料")
public class ReqPageMaterial {
    @ApiModelProperty("物料号")
    private String materialCode;
    @ApiModelProperty("ao工序号")
    private String aoCode;
}
