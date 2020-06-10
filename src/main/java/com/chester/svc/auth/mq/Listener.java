package com.chester.svc.auth.mq;

import com.chester.auth.client.model.AuthRuleInit;
import com.chester.svc.auth.access.mongodb.AuthRuleDao;
import com.chester.util.coll.Lists;
import com.chester.util.json.JSON;
import com.mongodb.MongoException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class Listener {

    @Resource
    private AuthRuleDao authRuleDao;
    @Resource
    private MqProducer mqProducer;

    @RabbitListener(queuesToDeclare = @Queue("q.auth.rule.init"))
    public void authRuleChange(Message msg) {
        AuthRuleInit authRule = JSON.parse(msg.getBody(), AuthRuleInit.class);
        Lists.each(authRule.getList(),v->{
            try{
                if(authRuleDao.initRule(v)){
                    mqProducer.autRuleChange(v);
                };
            }catch (MongoException e){
                log.info("MongoException",e);
            }
        });
    }
}
