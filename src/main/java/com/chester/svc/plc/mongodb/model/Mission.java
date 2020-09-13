package com.chester.svc.plc.mongodb.model;

import com.chester.cloud.support.model.BaseEntity;
import com.chester.data.mongo.annotations._id;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

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
    @ApiModelProperty("导入时间")
    private String inputTime;
    @ApiModelProperty("导入批次")
    private Integer batchNumber;
    @ApiModelProperty("物料号")
    private String materialCode;
    @ApiModelProperty("定额数量")
    private Integer quantity;
    @ApiModelProperty("生产站位")
    private String position;
    @ApiModelProperty("ao工序号")
    private String aoCode;
    @ApiModelProperty("机器Id")
    private String machineId;
    @ApiModelProperty("包装数量")
    private Integer count;
    @ApiModelProperty("转换状态 1未转换，2转换成功，3转换时找不到物料")
    private Integer transform;
    @ApiModelProperty("工单是否结束")
    private Boolean isFinish;
    @ApiModelProperty("当前工单 任务状态 0 未执行 1进行中 2 任务完成 3任务异常终止 4已删除")
    private Integer jobStatus;
    @ApiModelProperty("需求日期")
    private String date;
    @ApiModelProperty("需求时间")
    private String time;
    @ApiModelProperty("排程时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm")
    private Date dateTime;
    @ApiModelProperty("版本号")
    private Integer version;
}
