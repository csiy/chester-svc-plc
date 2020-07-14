package com.chester.svc.plc.mqtt.payload;

import lombok.Data;

@Data
public class HeartbeatPayload extends Payload{
    public HeartbeatPayload(){
        super("beat");
    }
}
