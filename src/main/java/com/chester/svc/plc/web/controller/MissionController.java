package com.chester.svc.plc.web.controller;

import com.chester.auth.client.annotation.Roles;
import com.chester.auth.client.core.UserTokenHolder;
import com.chester.svc.plc.mongodb.model.Material;
import com.chester.svc.plc.mongodb.model.Mission;
import com.chester.svc.plc.mongodb.repository.MaterialRepository;
import com.chester.svc.plc.mongodb.repository.MissionRepository;
import com.chester.svc.plc.mongodb.repository.SerialRepository;
import com.chester.svc.plc.web.model.req.ReqImportMission;
import com.chester.svc.plc.web.model.req.ReqMaterial;
import com.chester.svc.plc.web.model.req.ReqPageMission;
import com.chester.svc.plc.web.model.req.ReqVerifyMaterial;
import com.chester.svc.plc.web.model.res.ResVerifyMaterial;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/plc/missions")
public class MissionController {

    @Resource
    private MissionRepository missionRepository;
    @Resource
    private SerialRepository serialRepository;
    @Resource
    private MaterialRepository materialRepository;

    @PostMapping
    @Roles(value = "admin,operator", remark = "添加任务")
    public void addMission(@RequestBody Mission mission) {
        Material material = materialRepository.getMaterial(mission.getMaterialCode(),mission.getAoCode());
        Assert.notNull(material,"物料号或AO工序号不存在");
        missionRepository.addMission(mission, UserTokenHolder.getUserId());
    }

    @PostMapping("/import")
    @Roles(value = "admin,operator", remark = "导入任务")
    public void importMission(@RequestBody ReqImportMission importMission) {
        for(int i = 0;i<importMission.getMissions().size();i++){
            Mission mission = importMission.getMissions().get(i);
            Material material = materialRepository.getMaterial(mission.getMaterialCode(),mission.getAoCode());
            Assert.notNull(material,"物料号或AO工序号不存在");
        }
        missionRepository.importMission(importMission.getMissions(), UserTokenHolder.getUserId());
    }

    @PostMapping("/verify")
    @Roles(value = "admin,operator", remark = "校验任务")
    public List<ResVerifyMaterial> verifyMaterial(@RequestBody ReqVerifyMaterial verifyMaterial){
        List<ResVerifyMaterial> result = new ArrayList<>();
        for(int i = 0;i<verifyMaterial.getMaterials().size();i++){
            ReqMaterial mission = verifyMaterial.getMaterials().get(i);
            Material material = materialRepository.getMaterial(mission.getMaterialCode(),mission.getAoCode());
            if(material!=null){
                ResVerifyMaterial verify = new ResVerifyMaterial();
                verify.setAoCode(mission.getAoCode());
                verify.setMaterialCode(mission.getMaterialCode());
                verify.setResult("物料号或AO工序号不存在");
                result.add(verify);
            }
        }
        return result;
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
