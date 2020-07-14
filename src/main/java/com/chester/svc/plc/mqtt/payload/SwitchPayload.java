package com.chester.svc.plc.mqtt.payload;

import lombok.Data;

@Data
public class SwitchPayload extends Payload{
    private String key;
    public SwitchPayload(String key){
        super("switch");
        this.key = key;
    }
}
