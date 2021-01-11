package com.chester.svc.plc.db.model;

import com.chester.svc.support.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "plc_job")
public class Job extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("工单Id")
    private Long jobId;
    @ApiModelProperty("key")
    private String key;
    @OneToOne
    @ApiModelProperty("任务")
    private Mission mission;
    @ManyToOne
    @ApiModelProperty("机器")
    private Machine machine;
    @ManyToOne
    @ApiModelProperty("物料")
    private Material material;
    @ApiModelProperty("工单是否结束")
    private Boolean isFinish;
    @ApiModelProperty("当前工单 任务状态 0未执行 1进行中 2任务完成 3任务异常终止")
    private Integer jobStatus;
    @ApiModelProperty("是否异常")
    private Boolean isError;
    @ApiModelProperty("错误信息")
    private String errorMessage;
    @ApiModelProperty("完成数量")
    private Integer finishCount;
}
