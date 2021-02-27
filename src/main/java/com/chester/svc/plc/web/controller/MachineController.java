package com.chester.svc.plc.web.controller;

import com.chester.auth.client.annotation.Roles;
import com.chester.auth.client.core.UserTokenHolder;
import com.chester.svc.plc.mongodb.model.Machine;
import com.chester.svc.plc.mongodb.repository.MachineRepository;
import com.chester.svc.plc.mongodb.repository.MissionRepository;
import com.chester.svc.plc.web.model.req.ReqPageMachine;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.List;

@Api(tags="机器")
@ApiIgnore
@RestController
@RequestMapping("/plc/machines")
public class MachineController {

    @Resource
    private MachineRepository machineRepository;

    @Resource
    private MissionRepository missionRepository;

    @PutMapping
    @ApiOperation("修改设备")
    @Roles(value = "admin,operator", remark = "修改设备")
    public void putMachine(@RequestBody Machine machine) {
        machineRepository.updateMachine(machine, UserTokenHolder.getUserId());
    }

    @GetMapping
    @ApiOperation("查找设备")
    @Roles(value = "admin,operator", remark = "查找设备")
    public PageResult<Machine> findMachine(ReqPageMachine query, Pagination pagination) {
        return machineRepository.machinePageResult(query,pagination);
    }

    @GetMapping("/all")
    @ApiOperation("查找所有设备")
    @Roles(value = "admin,operator", remark = "查找所有设备")
    public List<Machine> findAllMachines(){
        return machineRepository.findAllMachines();
    }

    @PutMapping("/start/{machineId}/{discNo}")
    @ApiOperation("开始任务")
    @Roles(value = "admin,operator", remark = "开始任务")
    public void startMachine(@PathVariable("machineId") String machineId,@PathVariable("discNo")Integer discNo){
        machineRepository.runMachine(machineId,discNo);
    }

    @PutMapping("/stop/{machineId}/{discNo}")
    @ApiOperation("停止任务")
    @Roles(value = "admin,operator", remark = "停止任务")
    public void stopMachine(@PathVariable("machineId") String machineId,@PathVariable("discNo")Integer discNo){
        machineRepository.stopMachine(machineId,discNo,false);
        List<Machine.Disk> disks = machineRepository.getMachine(machineId).getDisks();
        missionRepository.updateMission(disks.get(discNo).getMissionId(), 4);
    }
}
