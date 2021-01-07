package com.chester.svc.plc.mqtt.payload;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ReceiverPayload extends Payload{

    private Long missionId;//任务Id
    private Integer missionStatus;//任务状态 0 当前无任务 1任务进行中 2 任务完成 3任务异常终止
    private String operation;
    private String operationResult;
    private String recvTTL;
    public ReceiverPayload(){

    }
}
