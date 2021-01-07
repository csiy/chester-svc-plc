package com.chester.svc.plc.mqtt.payload;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HeartbeatPayload extends Payload{
    public HeartbeatPayload(){
        super("beat");
    }
}
