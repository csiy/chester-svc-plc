package com.chester.svc.plc.mqtt.payload;

import lombok.Data;

@Data
public class Payload {
    private String msgType;
    private String TTL;
}
