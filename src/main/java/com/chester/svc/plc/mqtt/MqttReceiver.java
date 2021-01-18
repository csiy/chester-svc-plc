package com.chester.svc.plc.mqtt;

import com.chester.svc.plc.mongodb.model.Machine;
import com.chester.svc.plc.mongodb.repository.MachineRepository;
import com.chester.svc.plc.mqtt.payload.ReceiverPayload;
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
    public static final String P_S = "/PLC/SUBSCRIBE";

    @Resource
    private IMqttClient mqttClient;
    @Resource
    private MachineRepository machineRepository;
    private static final Map<String,String> subscribeMap = new HashMap<>();

    static {
        subscribeMap.put(P_S,P_S);
    }

    @PostConstruct
    public void init() {
        subscribe(H_B,subscribe -> {});
        subscribe(P_S,subscribe -> {
            SubscribePayload subscribePayload = JSON.parse(subscribe, SubscribePayload.class);
            machineRepository.addMachine(subscribePayload.getClientName());
            log.info("注册 ：{}",JSON.stringify(subscribePayload));
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
                machineRepository.linked(clientName);
                try{
                    String machineId = clientName;
                    ReceiverPayload receiverPayload = JSON.parse(info, ReceiverPayload.class);
                    if(receiverPayload.getMsgType().equals("taskStatus")){
                        machineRepository.updateMission(machineId,receiverPayload.getMissionId(),receiverPayload.getDiscNo(),receiverPayload.getMissionStatus());
                    }else if(receiverPayload.getMsgType().equals("reply")){
                        if(receiverPayload.getOperation().equals("open")&&receiverPayload.getOperationResult().equals("success")){
                            //machineRepository.updateRunStateConfirm(clientName,true);
                        }else if(receiverPayload.getOperation().equals("close")&&receiverPayload.getOperationResult().equals("success")){
                            //machineRepository.updateRunStateConfirm(clientName,false);
                        }else if(receiverPayload.getOperation().equals("setDisc")&&receiverPayload.getOperationResult().equals("success")){
                            //machineRepository.updateSetMissionConfirm(clientName);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
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
