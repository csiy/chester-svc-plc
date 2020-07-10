package com.chester.svc.plc.web.controller;

import com.chester.auth.client.annotation.Roles;
import com.chester.auth.client.core.UserTokenHolder;
import com.chester.svc.plc.mongodb.model.Mission;
import com.chester.svc.plc.mongodb.repository.MissionRepository;
import com.chester.svc.plc.mongodb.repository.SerialRepository;
import com.chester.svc.plc.web.model.req.ReqPageMission;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/plc/missions")
public class MissionController {

    @Resource
    private MissionRepository missionRepository;
    @Resource
    private SerialRepository serialRepository;

    @PostMapping
    @Roles(value = "admin,operator", remark = "添加任务")
    public void addMission(@RequestBody Mission mission) {
        missionRepository.addMission(mission, UserTokenHolder.getUserId());
    }

    @PostMapping("/import/{batchNumber}")
    @Roles(value = "admin,operator", remark = "添加任务")
    public void importMission(@RequestBody Mission mission,@PathVariable("batchNumber") Integer batchNumber) {
        missionRepository.addMission(mission, UserTokenHolder.getUserId(),batchNumber);
    }

    @GetMapping("/batchNumber")
    @Roles(value = "admin,operator", remark = "获取批次号")
    public Integer batchNumber(){
        return serialRepository.batchNumber();
    }

    @PutMapping
    @Roles(value = "admin,operator", remark = "修改任务")
    public void putMission(@RequestBody Mission mission) {
        missionRepository.updateMission(mission, UserTokenHolder.getUserId());
    }

    @GetMapping
    @Roles(value = "admin,operator", remark = "查找任务")
    public PageResult<Mission> findMission(ReqPageMission query, Pagination page) {
        return missionRepository.missionPageResult(query,page);
    }

    @DeleteMapping("/{missionId}/{version}")
    @Roles(value = "admin,operator", remark = "删除任务")
    public void deleteMission(@PathVariable("missionId") String missionId,@PathVariable("version")Integer version) {
        missionRepository.deleteMission(missionId,version,UserTokenHolder.getUserId());
    }

}
