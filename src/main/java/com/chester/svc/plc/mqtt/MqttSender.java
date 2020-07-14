package com.chester.svc.plc.mqtt;

import com.chester.svc.plc.mqtt.payload.Payload;
import com.chester.util.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class MqttSender {
    @Resource
    private IMqttClient mqttClient;

    public void sendMessage(String topic, Payload mqttPayload){
        try {
            mqttClient.publish(topic,JSON.stringify(mqttPayload).getBytes(),2,true);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
