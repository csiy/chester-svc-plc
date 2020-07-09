package com.chester.svc.plc.mongodb.model;

import com.chester.cloud.support.model.BaseEntity;
import com.chester.data.mongo.annotations._id;
import com.chester.svc.plc.core.model.JobStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("工单")
public class Job extends BaseEntity {
    @_id
    @ApiModelProperty("工单Id")
    private Long jobId;
    @ApiModelProperty("包装任务Id")
    private Long taskId;
    @ApiModelProperty("存储Id")
    private String storageId;
    @ApiModelProperty("存储区域")
    private String storageArea;
    @ApiModelProperty("存储BIN位")
    private String storageBin;
    @ApiModelProperty("存储版本号")
    private Integer storageVersion;
    @ApiModelProperty("物料Id")
    private String materialId;
    @ApiModelProperty("物料名称")
    private String materialName;
    @ApiModelProperty("物料描述")
    private String materialDescription;
    @ApiModelProperty("物料类型")
    private String materialType;
    @ApiModelProperty("物料挡位")
    private Integer materialGears;
    @ApiModelProperty("物料版本号")
    private Integer materialVersion;
    @ApiModelProperty("机器Id")
    private String machineId;
    @ApiModelProperty("机器名称")
    private String machineName;
    @ApiModelProperty("机器位置")
    private String machineAddress;
    @ApiModelProperty("震动盘盘号")
    private Integer dishNumber;
    @ApiModelProperty("物料数量")
    private Integer count;
    @ApiModelProperty("指派工人ID")
    private Long workId;
    @ApiModelProperty("指派工人姓名")
    private Long workName;
    @ApiModelProperty("工单状态")
    private JobStatus jobStatus;
    @ApiModelProperty("版本号")
    private Integer version;
}
