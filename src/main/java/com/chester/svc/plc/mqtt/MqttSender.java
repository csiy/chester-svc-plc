package com.chester.svc.plc.mqtt;

import com.chester.svc.plc.mqtt.payload.HeartbeatPayload;
import com.chester.svc.plc.mqtt.payload.Payload;
import com.chester.util.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.chester.svc.plc.mqtt.MqttReceiver.*;

@Component
@Slf4j
public class MqttSender {
    @Resource
    private IMqttClient mqttClient;

    public void sendMessage(String machineId, Payload mqttPayload){
        try {
            mqttClient.publish(S_C+machineId,JSON.stringify(mqttPayload).getBytes(),2,false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void testMessage(String machineId){
        try {
            HeartbeatPayload payload = new HeartbeatPayload();
            mqttClient.publish(C_S+machineId,JSON.stringify(payload).getBytes(),2,false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void sendBeat(){
        try {
            HeartbeatPayload payload = new HeartbeatPayload();
            mqttClient.publish(H_B,JSON.stringify(payload).getBytes(),2,false);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
