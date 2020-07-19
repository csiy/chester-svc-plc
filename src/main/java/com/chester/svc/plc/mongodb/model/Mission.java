package com.chester.svc.plc.mongodb.model;

import com.chester.cloud.support.model.BaseEntity;
import com.chester.data.mongo.annotations._id;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
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
    @ApiModelProperty("物料号")
    private String materialCode;
    @ApiModelProperty("ao工序号")
    private String aoCode;
    @ApiModelProperty("机器Id")
    private String machineId;
    @ApiModelProperty("包装数量")
    private Integer count;
    @ApiModelProperty("转换状态 1未转换，2转换成功，3转换时找不到物料")
    private Integer transform;
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
