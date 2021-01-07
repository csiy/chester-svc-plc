package com.chester.svc.plc.mongodb.model;

import com.chester.svc.support.model.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class Logs extends BaseEntity {
    @ApiModelProperty("日志类型")
    private LogsType logsType;
    @ApiModelProperty("备注信息")
    private String remark;
    @ApiModelProperty("修改后")
    private BaseEntity after;
    @ApiModelProperty("修改前")
    private BaseEntity before;
    public enum LogsType{
        MATERIAL,MACHINE,TASK,JOB
    }
}
