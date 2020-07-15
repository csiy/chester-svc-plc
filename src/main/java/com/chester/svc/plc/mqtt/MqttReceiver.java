package com.chester.svc.plc.mqtt;

import com.chester.svc.plc.mongodb.model.Machine;
import com.chester.svc.plc.mongodb.repository.MachineRepository;
import com.chester.svc.plc.mqtt.payload.HeartbeatPayload;
import com.chester.svc.plc.mqtt.payload.Payload;
import com.chester.svc.plc.mqtt.payload.ReplyPayload;
import com.chester.svc.plc.mqtt.payload.SubscribePayload;
import com.chester.util.coll.Lists;
import com.chester.util.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Component
public class MqttReceiver {

    public static final String C_S = "/PLC/C/S/";
    public static final String S_C = "/PLC/S/C/";
    public static final String H_B = "/PLC/S/H";

    @Resource
    private IMqttClient mqttClient;
    @Resource
    private MachineRepository machineRepository;
    @Resource
    private MqttSender mqttSender;
    private static final Map<String,String> subscribeMap = new HashMap<>();

    static {
        subscribeMap.put("/PLC/SUBSCRIBE","/PLC/SUBSCRIBE");
    }

    @PostConstruct
    public void init() {

        subscribe(H_B,subscribe -> {
            log.info("MQTT beat {}",System.currentTimeMillis());
        });

        subscribe("/PLC/SUBSCRIBE",subscribe -> {
            SubscribePayload subscribePayload = JSON.parse(subscribe, SubscribePayload.class);
            machineRepository.addMachine(subscribePayload.getClientName());
            initSubscribe(subscribePayload.getClientName());
        });
        List<Machine> machines = machineRepository.findAllMachines();
        Lists.each(machines,v->{
            initSubscribe(v.getMachineId());
        });
    }

    private void initSubscribe(String clientName){
        String key = C_S+clientName;
        String exist = subscribeMap.get(key);
        if(exist==null){
            subscribeMap.put(key,clientName);
            subscribe(key,info->{
                Payload payload = JSON.parse(info, Payload.class);
                ReplyPayload replyPayload = new ReplyPayload(payload.getMsgType(),"success",payload.getTTL());
                mqttSender.sendMessage(clientName,replyPayload);
                machineRepository.linked(clientName);
            });
        }
    }

    public void subscribe(String topic, Consumer<byte[]> consumer){
        try {
            mqttClient.subscribe(topic,0, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Payload payload = JSON.parse(message.getPayload(), Payload.class);
                    if(payload.getMsgType()!=null){
                        if(payload.getMsgType().equals("reply")){

                        }else{
                            consumer.accept(message.getPayload());
                        }
                    }
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
