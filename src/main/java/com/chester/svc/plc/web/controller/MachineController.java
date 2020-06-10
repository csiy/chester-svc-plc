package com.chester.svc.plc.web.controller;

import com.chester.auth.client.annotation.Roles;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/plc/machines")
public class MachineController {

    @PostMapping
    @Roles(value = "admin",remark = "添加设备")
    public void addMachine(){

    }
    @PutMapping
    @Roles(value = "admin",remark = "修改设备")
    public void putMachine(){

    }
    @GetMapping
    @Roles(value = "admin",remark = "查找设备")
    public void findMachine(){

    }
    @DeleteMapping
    @Roles(value = "admin",remark = "删除设备")
    public void deleteMachine(){

    }

}
