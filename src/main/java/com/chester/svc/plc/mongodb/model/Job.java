package com.chester.svc.plc.mongodb.model;

import com.chester.svc.support.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("工单")
public class Job extends BaseEntity {
    @ApiModelProperty("工单Id")
    private String jobId;
    @ApiModelProperty("物料Id")
    private String materialId;
    @ApiModelProperty("任务")
    private Mission mission;
    @ApiModelProperty("物料")
    private Material material;
    @ApiModelProperty("机器Id")
    private String machineId;
    @ApiModelProperty("工单是否结束")
    private Boolean isFinish;
    @ApiModelProperty("当前工单 任务状态 0未执行 1进行中 2任务完成 3任务异常终止")
    private Integer jobStatus;
    @ApiModelProperty("是否异常")
    private Boolean isError;
    @ApiModelProperty("错误信息")
    private List<String> errorMessages;
    @ApiModelProperty("完成数量")
    private Integer finishCount;
    @ApiModelProperty("指派工人ID")
    private Long workId;
    @ApiModelProperty("指派工人名称")
    private String workName;
    @ApiModelProperty("版本号")
    private Integer version;
}
