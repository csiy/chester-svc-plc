package com.chester.svc.plc.web.controller;

import com.chester.svc.plc.db.model.Machine;
import com.chester.svc.plc.db.repository.MachineRepository;
import com.chester.svc.plc.web.model.req.ReqPageMachine;
import com.chester.svc.sys.annotation.Roles;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/plc/machines")
public class MachineController {

    @Resource
    private MachineRepository machineRepository;

    @PutMapping
    @Roles(value = "admin,operator", remark = "修改设备")
    public void putMachine(@RequestBody Machine machine) {
    }

    @PutMapping("/dish")
    @Roles(value = "admin,operator", remark = "设置当前盘号")
    public void updateRuntimeDish(@RequestBody Machine machine) {
    }

    @GetMapping
    @Roles(value = "admin,operator", remark = "查找设备")
    public PageResult<Machine> findMachine(ReqPageMachine query, Pagination pagination) {
        return null;
    }

    @GetMapping("/all")
    @Roles(value = "admin,operator", remark = "查找所有设备")
    public List<Machine> findAllMachines() {
        return null;
    }

    @DeleteMapping("/{machineId}/{version}")
    @Roles(value = "admin,operator", remark = "删除设备")
    public void deleteMachine(@PathVariable("machineId") Long machineId, @PathVariable("version") Integer version) {
    }

    @PutMapping("/set/{machineId}/{jobId}")
    @Roles(value = "admin,operator", remark = "设置任务")
    public void setMachine(@PathVariable("machineId") Long machineId, @PathVariable("jobId") String jobId) {
    }

    @PutMapping("/start/{machineId}")
    @Roles(value = "admin,operator", remark = "开始")
    public void startMachine(@PathVariable("machineId") Long machineId) {
    }

    @PutMapping("/stop/{machineId}")
    @Roles(value = "admin,operator", remark = "停止")
    public void stopMachine(@PathVariable("machineId") Long machineId) {
    }
}
