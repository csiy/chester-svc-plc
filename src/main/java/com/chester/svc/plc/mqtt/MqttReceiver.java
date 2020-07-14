package com.chester.svc.plc.mqtt;

import com.chester.svc.plc.mongodb.repository.MachineRepository;
import com.chester.svc.plc.mqtt.payload.HeartbeatPayload;
import com.chester.svc.plc.mqtt.payload.SubscribePayload;
import com.chester.util.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Component
public class MqttReceiver {

    @Resource
    private IMqttClient mqttClient;
    @Resource
    private MachineRepository machineRepository;
    private static final Map<String,String> subscribeMap = new HashMap<>();

    static {
        subscribeMap.put("/PLC/SUBSCRIBE","/PLC/SUBSCRIBE");
    }

    @PostConstruct
    public void init() {
        subscribe("/PLC/SUBSCRIBE",subscribe -> {
            SubscribePayload subscribePayload = JSON.parse(subscribe, SubscribePayload.class);
            machineRepository.addMachine(subscribePayload.getClientName());
            initSubscribe("/PLC/C/S/"+subscribePayload.getClientName(),subscribePayload.getClientName());
        });
    }

    private void initSubscribe(String address,String clientName){
        String exist = subscribeMap.get(address);
        if(exist==null){
            subscribeMap.put(address,clientName);
            subscribe(address,info->{
                machineRepository.linked(clientName);
            });
        }
    }

    public void subscribe(String topic, Consumer<byte[]> consumer){
        try {
            mqttClient.subscribe(topic,0, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    consumer.accept(message.getPayload());
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
