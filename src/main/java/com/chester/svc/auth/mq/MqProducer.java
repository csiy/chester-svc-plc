package com.chester.svc.auth.mq;

import com.chester.auth.client.model.AuthRule;
import com.chester.util.json.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class MqProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;

    public void autRuleChange(AuthRule authRules) {
        this.sendFanoutMessage("e.auth.rules", authRules);
    }

    private void sendDelayedMessage(String exchange, String routeKey, Object data, int delay) {
        this.rabbitTemplate.convertAndSend(exchange, routeKey, JSON.serialize(data), (message) -> {
            message.getMessageProperties().setDelay(delay * 1000);
            return message;
        });
    }

    private void sendFanoutMessage(String exchange, Object data) {
        this.rabbitTemplate.convertAndSend(exchange, null, JSON.serialize(data));
    }

    private void sendQueueMessage(String routeKey, Object data) {
        this.rabbitTemplate.convertAndSend(routeKey, JSON.serialize(data));
    }
}
