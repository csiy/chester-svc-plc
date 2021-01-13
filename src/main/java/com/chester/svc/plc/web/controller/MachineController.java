package com.chester.svc.plc.web.controller;

import com.chester.svc.plc.db.model.Job;
import com.chester.svc.plc.db.model.Machine;
import com.chester.svc.plc.db.repository.JobRepository;
import com.chester.svc.plc.db.repository.MachineRepository;
import com.chester.svc.plc.web.model.req.ReqPageMachine;
import com.chester.svc.sys.annotation.Roles;
import com.chester.svc.sys.util.UserUtils;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/plc/machines")
public class MachineController {

    @Resource
    private MachineRepository machineRepository;
    @Resource
    private JobRepository jobRepository;

    @PutMapping
    @Roles(value = "admin,operator", remark = "修改设备")
    public void putMachine(@RequestBody Machine machine) {
        machineRepository.updateMachine(machine, UserUtils.getUserId());
    }

    @PutMapping("/dish")
    @Roles(value = "admin,operator", remark = "设置当前盘号")
    public void updateRuntimeDish(@RequestBody Machine machine) {
        machineRepository.updateRuntimeDish(machine, UserUtils.getUserId());
    }

    @GetMapping
    @Roles(value = "admin,operator", remark = "查找设备")
    public PageResult<Machine> findMachine(ReqPageMachine query, Pagination pagination) {
        return machineRepository.machinePageResult(query, pagination);
    }

    @GetMapping("/all")
    @Roles(value = "admin,operator", remark = "查找所有设备")
    public List<Machine> findAllMachines() {
        return machineRepository.findAllMachines();
    }

    @DeleteMapping("/{machineId}/{version}")
    @Roles(value = "admin,operator", remark = "删除设备")
    public void deleteMachine(@PathVariable("machineId") Long machineId, @PathVariable("version") Integer version) {
        machineRepository.deleteMachine(machineId, version, UserUtils.getUserId());
    }

    @PutMapping("/set/{machineId}/{jobId}")
    @Roles(value = "admin,operator", remark = "设置任务")
    public void setMachine(@PathVariable("machineId") Long machineId, @PathVariable("jobId") String jobId) {
        Machine machine = machineRepository.getMachine(machineId);
        Assert.notNull(machine, "机器不存在");
        Assert.notNull(machine.getDisk(), "请先设置当前盘");
        Job job = jobRepository.getJob(jobId);
        Assert.notNull(job, "排程不存在");
        Assert.isTrue(job.getMachine().getMachineId().equals("") || job.getJobId().equals(machine.getRuntimeJob()), "任务已被设置");
        Assert.isTrue(machine.getDisk().equals(job.getMaterial().getDisk()), "盘号不一致");
        machineRepository.setDish(machine, job);
    }

    @PutMapping("/start/{machineId}")
    @Roles(value = "admin,operator", remark = "开始")
    public void startMachine(@PathVariable("machineId") Long machineId) {
        machineRepository.runMachine(machineId);
    }

    @PutMapping("/stop/{machineId}")
    @Roles(value = "admin,operator", remark = "停止")
    public void stopMachine(@PathVariable("machineId") Long machineId) {
        machineRepository.stopMachine(machineId);
    }
}
