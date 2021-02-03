package com.chester.svc.plc.web.controller;

import com.chester.svc.plc.mongodb.model.Machine;
import com.chester.svc.plc.mongodb.model.Material;
import com.chester.svc.plc.mongodb.model.Mission;
import com.chester.svc.plc.mongodb.model.WM;
import com.chester.svc.plc.mongodb.repository.MachineRepository;
import com.chester.svc.plc.mongodb.repository.MaterialRepository;
import com.chester.svc.plc.mongodb.repository.MissionRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Api(tags="外部接口")
@RestController
@RequestMapping("/plc/wms")
public class WMSController {

    @Resource
    private MissionRepository missionRepository;
    @Resource
    private MaterialRepository materialRepository;
    @Resource
    private MachineRepository machineRepository;

    @PostMapping
    @ApiOperation("导入任务")
    public Boolean add(@RequestBody WM wms) {
        Material material = materialRepository.getMaterial(wms.getMaterialCode(), wms.getAoCode());
        Assert.notNull(material, "物料号或AO工序号不存在");
        Mission mission = new Mission();
        mission.setLineNumber(1);
        mission.setCount(wms.getCount());
        mission.initMaterial(material);
        mission.setSize(wms.getSize());
        mission.setSpeed(wms.getSpeed());
        mission.setWaveNo(wms.getWaveNo());
        mission.setVerify(wms.getVerify());
        missionRepository.addMission(mission, 10000L);
        return true;
    }

    @ApiOperation("打印任务")
    @PostMapping("/print/{waveNo}")
    public void print(@PathVariable("waveNo") String waveNo){
        Mission mission = missionRepository.getMissionByWave(waveNo);
        Assert.notNull(mission,"任务不存在");
        Machine machine = machineRepository.getMachine(mission.getMachineId());
        Assert.isTrue(StringUtils.hasLength(machine.getAddress()),"回调地址未设置");
        missionRepository.print(machine.getAddress(),mission);
    }

    @ApiOperation("获取任务")
    @PostMapping("/mission/{waveNo}")
    public Mission mission(@PathVariable("waveNo") String waveNo){
        return missionRepository.getMissionByWave(waveNo);
    }
}
