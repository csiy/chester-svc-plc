package com.chester.svc.sys.mq;

import com.chester.auth.client.model.Menu;
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
public class SysListener {

    @Resource
    private MenuRepository menuRepository;

    @RabbitListener(queuesToDeclare = @Queue("q.sys.menu.init"))
    public void authRuleChange(Message msg) {
        InputStream sbs = new ByteArrayInputStream(msg.getBody());
        List<Menu> menus = JSON.parseList(sbs, Menu.class);
        Lists.each(menus, v -> {
            try {
                menuRepository.addMenu(v, null);
            } catch (MongoException e) {
                log.info("MongoException", e);
            }
        });
    }
}
