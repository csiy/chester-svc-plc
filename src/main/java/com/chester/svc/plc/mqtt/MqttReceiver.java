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
import java.util.function.Consumer;

@Slf4j
@Component
public class MqttReceiver {

    @Resource
    private IMqttClient mqttClient;
    @Resource
    private MachineRepository machineRepository;

    @PostConstruct
    public void init() {
        subscribe("PLC/SUBSCRIBE",subscribe -> {
            log.info("PLC/SUBSCRIBE : {}",subscribe);
            log.info("PLC/SUBSCRIBE : {}",new String(subscribe));
            SubscribePayload subscribePayload = JSON.parse(subscribe, SubscribePayload.class);
            log.info("PLC/SUBSCRIBE : {}",subscribePayload);
            machineRepository.addMachine(subscribePayload.getClientName());
            subscribe(subscribePayload.getAddress(),info->{
                machineRepository.linked(subscribePayload.getClientName());
            });
        });
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
