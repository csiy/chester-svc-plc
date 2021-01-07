package com.chester.svc.plc.mqtt.payload;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SubscribePayload extends Payload{
    private String clientName;
    private String address;
    public SubscribePayload(){
        super("subscribe");
    }
}
