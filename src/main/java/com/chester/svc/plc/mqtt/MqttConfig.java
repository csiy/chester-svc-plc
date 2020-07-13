package com.chester.svc.plc.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

import javax.annotation.Resource;
import java.util.UUID;

@Configuration
@Slf4j
public class MqttConfig {
    protected final String serverURI = "tcp://39.99.199.83:1883";
    protected final String userName = "plcmos";
    protected final String password = "plcmos@1234";

    @Resource
    private MqttReceiver mqttReceiver;

    @Bean
    public MqttClient getMqttClient(){
        try {
            MqttClient client = new MqttClient(serverURI,UUID.randomUUID().toString(),new MemoryPersistence());
            client.connect(getConnectionOptions());
            return client;
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean
    public MqttConnectOptions getConnectionOptions(){
        MqttConnectOptions mqttConnectOptions=new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setConnectionTimeout(0);
        mqttConnectOptions.setKeepAliveInterval(120);
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setUserName(userName);
        mqttConnectOptions.setPassword(password.toCharArray());
        mqttConnectOptions.setServerURIs(new String[]{serverURI});
        mqttConnectOptions.setKeepAliveInterval(2);
        return mqttConnectOptions;
    }

}