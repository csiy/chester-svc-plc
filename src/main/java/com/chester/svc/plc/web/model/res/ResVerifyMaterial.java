package com.chester.svc.plc.web.model.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("校验结果")
public class ResVerifyMaterial {
    @ApiModelProperty("物料号")
    private String materialCode;
    @ApiModelProperty("ao工序号")
    private String aoCode;
    @ApiModelProperty("异常结果")
    private String result;
}
