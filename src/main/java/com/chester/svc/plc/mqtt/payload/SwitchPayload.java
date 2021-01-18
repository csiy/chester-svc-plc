package com.chester.svc.plc.mqtt.payload;

import lombok.Data;

@Data
public class SwitchPayload extends Payload{
    private String key;
    private Integer discNo;
    public SwitchPayload(String key,Integer discNo){
        super("switch");
        this.key = key;
        this.discNo = discNo;
    }
}
