package com.chester.svc.plc.mongodb.model;

import com.chester.cloud.support.model.BaseEntity;
import com.chester.data.mongo.annotations._id;
import com.chester.svc.plc.core.model.JobStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("工单")
public class Job extends BaseEntity {
    @_id
    @ApiModelProperty("工单Id")
    private String jobId;
    @ApiModelProperty("任务")
    private Mission mission;
    @ApiModelProperty("物料")
    private Material material;
    @ApiModelProperty("机器Id")
    private String machineId;
    @ApiModelProperty("工单状态")
    private JobStatus jobStatus;
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
