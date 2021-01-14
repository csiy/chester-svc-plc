package com.chester.svc.plc.web.controller;

import com.chester.svc.plc.db.model.Material;
import com.chester.svc.plc.db.model.Mission;
import com.chester.svc.plc.db.repository.MaterialRepository;
import com.chester.svc.plc.db.repository.MissionRepository;
import com.chester.svc.plc.web.model.req.ReqImportMission;
import com.chester.svc.plc.web.model.req.ReqMaterial;
import com.chester.svc.plc.web.model.req.ReqPageMission;
import com.chester.svc.plc.web.model.req.ReqVerifyMaterial;
import com.chester.svc.plc.web.model.res.ResVerifyMaterial;
import com.chester.svc.sys.annotation.Roles;
import com.chester.svc.sys.util.UserUtils;
import com.chester.util.coll.Lists;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/plc/missions")
public class MissionController {

    @Resource
    private MissionRepository missionRepository;
    @Resource
    private MaterialRepository materialRepository;

    @PostMapping
    @Roles(value = "admin,operator", remark = "添加任务")
    public void addMission(@RequestBody Mission mission) {
        missionRepository.save(mission);
    }

    @PostMapping("/import")
    @Roles(value = "admin,operator", remark = "导入任务")
    public void importMission(@RequestBody ReqImportMission importMission) {
        List<String> keys = Lists.map(importMission.getMissions(),v->v.getAoCode()+","+v.getMaterialCode());
        keys = keys.stream().distinct().collect(Collectors.toList());
        for (String key : keys) {
            String[] codes = key.split(",");
            String aoCode = codes[0];
            String materialCode = codes[1];
            Material material = materialRepository.getByMaterialCodeAndAoCode(materialCode, aoCode);
            Assert.notNull(material, "物料号或AO工序号不存在");
        }
        missionRepository.saveAll(importMission.getMissions());
    }

    @PostMapping("/verify")
    @Roles(value = "admin,operator", remark = "校验任务")
    public List<ResVerifyMaterial> verifyMaterial(@RequestBody ReqVerifyMaterial verifyMaterial) {
        List<ResVerifyMaterial> result = new ArrayList<>();
        for (int i = 0; i < verifyMaterial.getMaterials().size(); i++) {
            ReqMaterial mission = verifyMaterial.getMaterials().get(i);
            Material material = materialRepository.getByMaterialCodeAndAoCode(mission.getMaterialCode(), mission.getAoCode());
            if (material != null) {
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
        missionRepository.save(mission);
    }

    @GetMapping
    @Roles(value = "admin,operator", remark = "查找任务")
    public PageResult<Mission> findMission(ReqPageMission query, Pagination page) {
        return null;
    }

    @DeleteMapping("/{missionId}/{version}")
    @Roles(value = "admin,operator", remark = "删除任务")
    public void deleteMission(@PathVariable("missionId") String missionId, @PathVariable("version") Integer version) {
    }

}
