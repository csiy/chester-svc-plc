package com.chester.svc.plc.web.controller;

import com.chester.auth.client.core.UserTokenHolder;
import com.chester.svc.plc.mongodb.model.Material;
import com.chester.svc.plc.mongodb.model.Mission;
import com.chester.svc.plc.mongodb.model.WMS;
import com.chester.svc.plc.mongodb.repository.MaterialRepository;
import com.chester.svc.plc.mongodb.repository.MissionRepository;
import com.chester.svc.plc.mongodb.repository.WMSRepository;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/plc/wms")
public class WMSController {

    @Resource
    private WMSRepository wmsRepository;
    @Resource
    private MissionRepository missionRepository;
    @Resource
    private MaterialRepository materialRepository;

    @PostMapping
    public void add(@RequestBody WMS wms) {
        Material material = materialRepository.getMaterial(wms.getSKU(), wms.getSOREFERENCE3());
        Assert.notNull(material, "物料号或AO工序号不存在");
        Mission mission = new Mission();
        mission.setLineNumber(1);
        mission.setCount(Integer.parseInt(wms.getQty_each()));
        mission.initMaterial(material);
        missionRepository.addMission(mission, UserTokenHolder.getUserId());
        wms.setMissionId(mission.getMissionId());
        wmsRepository.add(wms);
    }
}
