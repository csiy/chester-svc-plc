package com.chester.svc.plc.mqtt.payload;

import lombok.Data;

@Data
public class SubscribePayload extends Payload{
    private String clientName;
    private String address;
}
