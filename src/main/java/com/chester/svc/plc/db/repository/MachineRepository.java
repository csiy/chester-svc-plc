package com.chester.svc.plc.db.repository;

import com.chester.svc.plc.db.model.Job;
import com.chester.svc.plc.db.model.Machine;
import com.chester.svc.plc.web.model.req.ReqPageMachine;
import com.chester.svc.sys.db.repository.UserRepository;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Repository
public class MachineRepository {
    @Resource
    private UserRepository userRepository;
    @Resource
    private JobRepository jobRepository;
    @Resource
    private MissionRepository missionRepository;

    public void addMachine(String clientName) {

    }

    public Machine getMachine(Long machineId) {
        return null;
    }

    public void deleteMachine(Long machineId, Integer version, Long updatedBy) {

    }

    public void updateMachine(Machine machine, Long updatedBy) {

    }

    /**
     * 修改当前盘号
     */
    public void updateRuntimeDish(Machine machine, Long updatedBy) {

    }

    public PageResult<Machine> machinePageResult(ReqPageMachine query, Pagination pagination) {
        return null;
    }

    public List<Machine> findAliveMachines() {
        return null;
    }

    public List<Machine> findAllMachines() {
        return null;
    }

    public List<Machine> findUnLinked() {
        return null;
    }

    public void linked(String key) {

    }

    public void unLinked(String key) {

    }

    public void updateMission(Long missionId, Integer missionStatus) throws InterruptedException {

    }

    public void runMachine(Long machineId) {
        log.info("开启机器");
        updateRunStateConfirm(machineId, true);
    }

    public void stopMachine(Long machineId) {
        log.info("停止机器");
        updateRunStateConfirm(machineId, false);
    }

    public void setDish(Machine machine, Job job) {
        jobRepository.setJobMachine(job.getJobId(), machine.getMachineId());
        updateSetMissionConfirm(machine.getMachineId());
    }

    public void updateRunStateConfirm(Long machineId, Boolean runState) {

    }

    public void updateSetMissionConfirm(Long machineId) {

    }
}
