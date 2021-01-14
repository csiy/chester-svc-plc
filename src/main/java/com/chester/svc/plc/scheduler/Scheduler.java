package com.chester.svc.plc.scheduler;

import com.chester.svc.plc.db.repository.MachineRepository;
import com.chester.svc.plc.db.repository.MaterialRepository;
import com.chester.svc.plc.db.repository.MissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
@EnableScheduling
public class Scheduler {

    @Resource
    private MachineRepository machineRepository;


    //每10秒执行超时处理
    @Scheduled(fixedRate = 5000)
    public void lostConnect() {
    }

}
