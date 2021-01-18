package com.chester.svc.plc.web.controller;

import com.chester.auth.client.annotation.Roles;
import com.chester.auth.client.core.UserTokenHolder;
import com.chester.svc.plc.mongodb.model.Machine;
import com.chester.svc.plc.mongodb.repository.MachineRepository;
import com.chester.svc.plc.web.model.req.ReqPageMachine;
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
        machineRepository.updateMachine(machine, UserTokenHolder.getUserId());
    }

    @GetMapping
    @Roles(value = "admin,operator", remark = "查找设备")
    public PageResult<Machine> findMachine(ReqPageMachine query, Pagination pagination) {
        return machineRepository.machinePageResult(query,pagination);
    }

    @GetMapping("/all")
    @Roles(value = "admin,operator", remark = "查找所有设备")
    public List<Machine> findAllMachines(){
        return machineRepository.findAllMachines();
    }

    @PutMapping("/set/{machineId}/{discNo}")
    @Roles(value = "admin,operator", remark = "设置任务")
    public void setMachine(@PathVariable("machineId") String machineId,@PathVariable("discNo") Integer discNo){
        machineRepository.setNext(machineId,discNo);
    }

    @PutMapping("/start/{machineId}/{discNo}")
    @Roles(value = "admin,operator", remark = "开始")
    public void startMachine(@PathVariable("machineId") String machineId,@PathVariable("discNo")Integer discNo){
        machineRepository.runMachine(machineId,discNo);
    }

    @PutMapping("/stop/{machineId}/{discNo}")
    @Roles(value = "admin,operator", remark = "停止")
    public void stopMachine(@PathVariable("machineId") String machineId,@PathVariable("discNo")Integer discNo){
        machineRepository.stopMachine(machineId,discNo);
    }
}
