package com.chester.svc.plc.mqtt.payload;

import lombok.Data;

@Data
public class ReceiverPayload extends Payload{

    private String missionId;//任务Id
    private Integer missionStatus;//任务状态 0 当前无任务 1任务进行中 2 任务完成 3任务异常终止
    private Integer discNo;//盘号
    private String operation;
    private String operationResult;
    private String recvTTL;
    public ReceiverPayload(){

    }
}
