package com.chester.svc.plc.web.controller;

import com.chester.auth.client.Roles;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/plc/missions")
public class MissionController {

    @PostMapping
    @Roles(value = "admin",remark = "添加任务")
    public void addMission(){

    }
    @PutMapping
    @Roles(value = "admin",remark = "修改任务")
    public void putMission(){

    }
    @GetMapping
    @Roles(value = "admin",remark = "查找任务")
    public void findMission(){

    }
    @DeleteMapping
    @Roles(value = "admin",remark = "删除任务")
    public void deleteMission(){

    }
    
}
