package com.chester.svc.plc.db.model;

import com.chester.svc.sys.db.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "plc_run_time")
public class RunTime extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("运行时Id")
    private Long runTimeId;
    @OneToOne
    @ApiModelProperty("任务")
    private Mission mission;
    @ManyToOne
    @ApiModelProperty("机器")
    private Machine machine;
    @ManyToOne
    @ApiModelProperty("物料")
    private Material material;
    @ApiModelProperty("当前工单 任务状态 0未执行 1进行中 2任务完成 3任务异常终止")
    private Integer status;
    @ApiModelProperty("完成数量")
    private Integer count;
}
