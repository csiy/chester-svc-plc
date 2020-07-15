package com.chester.svc.plc.mqtt.payload;

import lombok.Data;

@Data
public class ReplyPayload extends Payload{
    private String recvTTL;
    private String operation;
    private String operationResult;
    public ReplyPayload(String operation,String operationResult,String recvTTL){
        super("reply");
    }
}
