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

    @DeleteMapping("/{machineId}/{version}")
    @Roles(value = "admin,operator", remark = "删除设备")
    public void deleteMachine(@PathVariable("machineId") String machineId,@PathVariable("version")Integer version) {
        machineRepository.deleteMachine(machineId,version,UserTokenHolder.getUserId());
    }

}
