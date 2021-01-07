package com.chester.svc.plc.mongodb.repository;

import com.chester.svc.plc.mongodb.config.Constant;
import com.chester.svc.plc.mongodb.model.Job;
import com.chester.svc.plc.mongodb.model.Machine;
import com.chester.svc.plc.mqtt.MqttSender;
import com.chester.svc.plc.mqtt.payload.DiscPayload;
import com.chester.svc.plc.mqtt.payload.SwitchPayload;
import com.chester.svc.plc.web.model.req.ReqPageMachine;
import com.chester.svc.sys.db.repository.UserRepository;
import com.chester.util.json.JSON;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class MachineRepository {
    @Resource
    private UserRepository userRepository;
    @Resource
    private JobRepository jobRepository;
    @Resource
    private MqttSender mqttSender;
    @Resource
    private MissionRepository missionRepository;

    public void addMachine(String clientName){

    }

    public Machine getMachine(Long machineId){
        return null;
    }

    public void deleteMachine(Long machineId,Integer version, Long updatedBy){

    }

    public void updateMachine(Machine machine, Long updatedBy){

    }

    /**
     * 修改当前盘号
     */
    public void updateRuntimeDish(Machine machine, Long updatedBy){

    }

    public PageResult<Machine> machinePageResult(ReqPageMachine query, Pagination pagination) {
        return null;
    }

    public List<Machine> findAliveMachines(){
        return null;
    }

    public List<Machine> findAllMachines(){
        return null;
    }

    public List<Machine> findUnLinked(){
        return null;
    }

    public void linked(String key){

    }

    public void unLinked(String key){

    }

    public void updateMission(Long missionId,Integer missionStatus) throws InterruptedException {

    }

    public void runMachine(Long machineId){
        log.info("开启机器");
        mqttSender.sendMessage(machineId, new SwitchPayload("open"));
        updateRunStateConfirm(machineId,true);
    }

    public void stopMachine(Long machineId){
        log.info("停止机器");
        mqttSender.sendMessage(machineId, new SwitchPayload("close"));
        updateRunStateConfirm(machineId,false);
    }

    public void setDish(Machine machine,Job job){
        jobRepository.setJobMachine(job.getJobId(),machine.getMachineId());
        DiscPayload.SetDiscList setDiscList = new DiscPayload.SetDiscList();
        setDiscList.setDiscNo(machine.getDiskList().indexOf(machine.getDisk())+1);
        log.info("set disc diskList:{},disk:{},discNo:{}", JSON.stringify(machine.getDiskList()),machine.getDisk(),setDiscList.getDiscNo());
        setDiscList.setTotalB(job.getMission().getCount());
        setDiscList.setTotalOneB(job.getMaterial().getQuantity());
        mqttSender.sendMessage(machine.getMachineId(), new DiscPayload(job.getJobId(),setDiscList));
        updateSetMissionConfirm(machine.getMachineId());
    }

    public void updateRunStateConfirm(Long machineId,Boolean runState){

    }

    public void updateSetMissionConfirm(Long machineId){

    }
}
