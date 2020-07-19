package com.chester.svc.plc.web.model.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("任务")
public class ReqPageMission {
    @ApiModelProperty("物料号")
    private String materialCode;
    @ApiModelProperty("ao工序号")
    private String aoCode;
    @ApiModelProperty("转换状态 1未转换，2转换成功，3转换时找不到物料")
    private Integer transform;
}
