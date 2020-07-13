package com.chester.svc.plc.mqtt;

import com.chester.util.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class MqttSender {
    @Resource
    private IMqttClient mqttClient;

    public void sendMessage(String topic,MqttPayload mqttPayload){
        MqttMessage mqttmessage = new MqttMessage(JSON.stringify(mqttPayload).getBytes());
        try {
            mqttClient.publish(topic,mqttmessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
