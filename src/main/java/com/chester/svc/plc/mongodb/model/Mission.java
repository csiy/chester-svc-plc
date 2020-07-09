package com.chester.svc.plc.mongodb.model;

import com.chester.cloud.support.model.BaseEntity;
import com.chester.data.mongo.annotations._id;
import com.chester.svc.plc.core.model.TaskStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("包装任务")
public class Mission extends BaseEntity {
    @_id
    @ApiModelProperty("任务Id")
    private String missionId;
    @ApiModelProperty("流水号")
    private Long serialNumber;
    @ApiModelProperty("行项目号")
    private Integer lineNumber;
    @ApiModelProperty("批次号")
    private Integer batchNumber;
    @ApiModelProperty("物料号")
    private String materialCode;
    @ApiModelProperty("ao工序号")
    private String aoCode;
    @ApiModelProperty("机器Id")
    private String machineId;
    @ApiModelProperty("包装数量")
    private Integer count;
    @ApiModelProperty("任务状态")
    private TaskStatus status;
    @ApiModelProperty("完成数量")
    private Integer finishCount;
    @ApiModelProperty("任务优先级")
    private Integer order;
    @ApiModelProperty("异常信息")
    private List<String> errorMsg;
    @ApiModelProperty("版本号")
    private Integer version;
}
