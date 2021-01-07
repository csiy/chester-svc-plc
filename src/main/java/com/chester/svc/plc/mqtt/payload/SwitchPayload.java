package com.chester.svc.plc.mqtt.payload;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SwitchPayload extends Payload{
    private String key;
    public SwitchPayload(String key){
        super("switch");
        this.key = key;
    }
}
