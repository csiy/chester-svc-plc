package com.chester.svc.log.mq;

import com.chester.auth.client.aop.Logs;
import com.chester.auth.client.model.Menu;
import com.chester.svc.log.mongodb.repository.LogRepository;
import com.chester.svc.sys.mongodb.repository.MenuRepository;
import com.chester.util.coll.Lists;
import com.chester.util.json.JSON;
import com.mongodb.MongoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Component
public class LogListener {

    @Resource
    private LogRepository logRepository;

    @RabbitListener(queuesToDeclare = @Queue("q.logs.request"))
    public void authRuleChange(Message msg) {
        Logs logs = JSON.parse(msg.getBody(), Logs.class);
        logRepository.addLog(logs);
    }
}
