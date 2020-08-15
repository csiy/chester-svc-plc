package com.chester.svc.plc.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MqttConfig {
    protected final String serverURI = "tcp://mosquitto:1883";
    protected final String userName = "plcmos";
    protected final String password = "plcmos@1234";

    @Bean
    public MqttClient getMqttClient(){
        try {
            MqttClient client = new MqttClient(serverURI,MqttAsyncClient.generateClientId(),new MemoryPersistence());
            client.connect(getConnectionOptions());
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    try {
                        client.reconnect();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
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