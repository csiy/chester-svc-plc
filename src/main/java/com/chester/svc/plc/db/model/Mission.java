package com.chester.svc.plc.db.model;

import com.chester.svc.sys.db.model.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "plc_mission")
@ApiModel("包装任务")
public class Mission extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("任务Id")
    private Long missionId;
    @ApiModelProperty("行项目号")
    private Integer lineNumber;
    @ApiModelProperty("导入时间")
    private String inputTime;
    @ApiModelProperty("导入批次")
    private Integer batchNumber;
    @ApiModelProperty("排程时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date dateTime;
    @ApiModelProperty("机器编码")
    private String machineCode;
    @ApiModelProperty("物料号")
    private String materialCode;
    @ApiModelProperty("ao工序号")
    private String aoCode;
    @ApiModelProperty("盘号")
    private String disk;
    @ApiModelProperty("定额数量")
    private Integer quantity;
    @ApiModelProperty("生产站位")
    private String position;
    @ApiModelProperty("存储BIN位")
    private String bin;
    @ApiModelProperty("包装数量")
    private Integer count;
    @ApiModelProperty("当前工单 任务状态 0未执行 1进行中 2任务完成 3任务异常终止")
    private Integer status;
}
