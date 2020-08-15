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
    @ApiModelProperty("当前工单 任务状态 0 未执行 1进行中 2 任务完成 3任务异常终止")
    private Integer jobStatus;
    @ApiModelProperty("查询日期")
    private String date;
}
