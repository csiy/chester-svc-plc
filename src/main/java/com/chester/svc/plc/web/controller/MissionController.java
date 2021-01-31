package com.chester.svc.plc.web.controller;

import com.chester.auth.client.annotation.Roles;
import com.chester.auth.client.core.UserTokenHolder;
import com.chester.svc.plc.mongodb.model.Machine;
import com.chester.svc.plc.mongodb.model.Material;
import com.chester.svc.plc.mongodb.model.Mission;
import com.chester.svc.plc.mongodb.repository.MachineRepository;
import com.chester.svc.plc.mongodb.repository.MaterialRepository;
import com.chester.svc.plc.mongodb.repository.MissionRepository;
import com.chester.svc.plc.web.model.req.ReqImportMission;
import com.chester.svc.plc.web.model.req.ReqMaterial;
import com.chester.svc.plc.web.model.req.ReqPageMission;
import com.chester.svc.plc.web.model.req.ReqVerifyMaterial;
import com.chester.svc.plc.web.model.res.ResVerifyMaterial;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Api(tags="任务")
@ApiIgnore
@RestController
@RequestMapping("/plc/missions")
public class MissionController {

    @Resource
    private MissionRepository missionRepository;
    @Resource
    private MaterialRepository materialRepository;
    @Resource
    private MachineRepository machineRepository;

    @PostMapping
    @ApiOperation("添加任务")
    @Roles(value = "admin,operator", remark = "添加任务")
    public void addMission(@RequestBody Mission mission) {
        Material material = materialRepository.getMaterial(mission.getMaterialCode(), mission.getAoCode());
        Assert.notNull(material, "物料号或AO工序号不存在");
        mission.initMaterial(material);
        missionRepository.addMission(mission, UserTokenHolder.getUserId());
    }

    @PostMapping("/import")
    @ApiOperation("导入任务")
    @Roles(value = "admin,operator", remark = "导入任务")
    public void importMission(@RequestBody ReqImportMission importMission) {
        for (int i = 0; i < importMission.getMissions().size(); i++) {
            Mission mission = importMission.getMissions().get(i);
            Material material = materialRepository.getMaterial(mission.getMaterialCode(), mission.getAoCode());
            Assert.notNull(material, "物料号或AO工序号不存在");
            mission.initMaterial(material);
        }
        missionRepository.importMission(importMission.getMissions(), UserTokenHolder.getUserId());
    }

    @PostMapping("/verify")
    @ApiOperation("校验任务")
    @Roles(value = "admin,operator", remark = "校验任务")
    public List<ResVerifyMaterial> verifyMaterial(@RequestBody ReqVerifyMaterial verifyMaterial) {
        List<ResVerifyMaterial> result = new ArrayList<>();
        for (int i = 0; i < verifyMaterial.getMaterials().size(); i++) {
            ReqMaterial mission = verifyMaterial.getMaterials().get(i);
            Material material = materialRepository.getMaterial(mission.getMaterialCode(), mission.getAoCode());
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
    @ApiOperation("修改任务")
    @Roles(value = "admin,operator", remark = "修改任务")
    public void putMission(@RequestBody Mission mission) {
        Material material = materialRepository.getMaterial(mission.getMaterialCode(), mission.getAoCode());
        Assert.notNull(material, "物料号或AO工序号不存在");
        mission.initMaterial(material);
        missionRepository.updateMission(mission, UserTokenHolder.getUserId());
    }

    @GetMapping("/{missionId}")
    @ApiOperation("查找任务")
    @Roles(value = "admin,operator", remark = "查找任务")
    public Mission getMission(@PathVariable("missionId") String missionId){
        return missionRepository.getMission(missionId);
    }

    @GetMapping
    @ApiOperation("查找任务列表")
    @Roles(value = "admin,operator", remark = "查找任务列表")
    public PageResult<Mission> findMission(ReqPageMission query, Pagination page) {
        return missionRepository.missionPageResult(query, page);
    }

    @ApiOperation("查找已排程任务")
    @GetMapping("/{machineId}/{disk}")
    @Roles(value = "admin,operator", remark = "查找已排程任务")
    public PageResult<Mission> findMissionByMachineIdAndDisk(
            @PathVariable("machineId") String machineId,
            @PathVariable("disk") String disk
            , Pagination page) {
        return missionRepository.findMissionByMachineIdAndDisk(machineId, disk, page);
    }

    @ApiOperation("查找未排程任务")
    @GetMapping("/unScheduler")
    @Roles(value = "admin,operator", remark = "查找未排程任务")
    public PageResult<Mission> unScheduler(Pagination page) {
        return missionRepository.unScheduler(page);
    }



    @DeleteMapping("/{missionId}")
    @ApiOperation("删除任务")
    @Roles(value = "admin,operator", remark = "删除任务")
    public void deleteMission(@PathVariable("missionId") String missionId) {
        missionRepository.deleteMission(missionId, UserTokenHolder.getUserId());
    }

    @ApiOperation("打印任务")
    @PostMapping("/print/{missionId}")
    public void print(@PathVariable("missionId") String missionId){
        Mission mission = missionRepository.getMission(missionId);
        Assert.notNull(mission,"任务不存在");
        Machine machine = machineRepository.getMachine(mission.getMachineId());
        Assert.isTrue(StringUtils.hasLength(machine.getAddress()),"回调地址为设置");
        missionRepository.print(machine.getAddress(),mission);
    }

}
