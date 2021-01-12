package com.chester.svc.plc.db.model;

import com.chester.svc.support.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel("机器")
@Entity
@Table(name = "plc_machine")
public class Machine extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("机器Id")
    private Long machineId;
    @ApiModelProperty("key")
    private String key;
    @ApiModelProperty("位置")
    private String address;
    @ApiModelProperty("机器盘")
    private String diskList;
    @ApiModelProperty("版本号")
    private Integer version;
    @ApiModelProperty("当前盘号")
    private String disk;
    @ApiModelProperty("当前工单")
    private String runtimeJob;
    @ApiModelProperty("当前工单参数设置状态")
    private Integer runtimeJobSetStatus;
    @ApiModelProperty("当前物料号")
    private String runtimeMaterialCode;
    @ApiModelProperty("当前工单 任务状态 0 当前无任务 1任务进行中 2 任务完成 3任务异常终止")
    private Integer runtimeJobStatus;
    @ApiModelProperty("机器链接状态")
    private Boolean linkState;
    @ApiModelProperty("机器运行状态")
    private Boolean runState;
    @ApiModelProperty("机器错误状态")
    private Integer errorCode;
    @ApiModelProperty("链接超时时间")
    private Long lostThreshold;
}
