package com.chester.svc.plc.web.controller;

import com.chester.svc.plc.mongodb.model.Material;
import com.chester.svc.plc.mongodb.model.Mission;
import com.chester.svc.plc.mongodb.model.WM;
import com.chester.svc.plc.mongodb.repository.MaterialRepository;
import com.chester.svc.plc.mongodb.repository.MissionRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(tags="外部接口")
@RestController
@RequestMapping("/plc/wms")
public class WMSController {

    @Resource
    private MissionRepository missionRepository;
    @Resource
    private MaterialRepository materialRepository;

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
        missionRepository.addMission(mission, 10000L);
        return true;
    }
}
