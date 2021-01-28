package com.chester.svc.plc.scheduler;

import com.chester.svc.plc.mongodb.model.Machine;
import com.chester.svc.plc.mongodb.model.Mission;
import com.chester.svc.plc.mongodb.repository.MachineRepository;
import com.chester.svc.plc.mongodb.repository.MissionRepository;
import com.chester.svc.plc.mqtt.MqttSender;
import com.chester.util.coll.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@EnableScheduling
public class Scheduler {

    @Resource
    private MachineRepository machineRepository;
    @Resource
    private MissionRepository missionRepository;
    @Resource
    private MqttSender mqttSender;

    //执行计划单转化任务
    @Scheduled(fixedRate = 1000)
    public void schedulerTransform() {
        List<Mission> missions = missionRepository.findAllUnSchedule();
        List<Machine> machines = machineRepository.findLinked();
        if(!Lists.isEmpty(missions)&&!Lists.isEmpty(machines)){
            Map<String,List<Machine>> diskMap = new HashMap<>();
            Lists.each(machines,v-> Lists.each(v.getDisks(), j->{
                List<Machine> list = diskMap.computeIfAbsent(j.getName(), k -> new ArrayList<>());
                list.add(v);
            }));
            diskMap.forEach((v,k)->{
                if(v!=null&&!Lists.isEmpty(k)){
                    List<Mission> list = Lists.filter(missions,j->j.getDisk().equals(v));
                    List<List<Mission>> averageList = averageAssign(list,k.size());
                    for(int i=0;i<averageList.size();i++){
                        Machine machine = k.get(i);
                        for(int j=0;j<averageList.get(i).size();j++){
                            missionRepository.updateMission(Lists.map(averageList.get(i),Mission::getMissionId),machine.getMachineId());
                        }
                    }
                }
            });
        }
    }

    //每10秒执行超时处理
    @Scheduled(fixedRate = 1000)
    public void lostConnect(){
        List<Machine> list = machineRepository.findUnLinked();
        Lists.each(list,v-> machineRepository.unLinked(v.getMachineId()));
    }

    //每10秒执行超时处理
    @Scheduled(fixedRate = 5000)
    public void testConnect(){
        mqttSender.sendBeat();
    }

    /**
     * 将一个list均分成n个list,主要通过偏移量来实现的
     */
    public static <T> List<List<T>> averageAssign(List<T> source, int n) {
        List<List<T>> result = new ArrayList<>();
        int remaider = source.size() % n;  //(先计算出余数)
        int number = source.size() / n;  //然后是商
        int offset = 0;//偏移量
        for (int i = 0; i < n; i++) {
            List<T> value;
            if (remaider > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remaider--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }


}
