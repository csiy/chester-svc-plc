package com.chester.svc.plc.mqtt.payload;

import lombok.Data;

@Data
public class Payload {

    private String msgType;
    private String ttl;

    public Payload(){

    }
    public Payload(String msgType){
        this.msgType = msgType;
        this.ttl = System.currentTimeMillis()+"";
    }
}
