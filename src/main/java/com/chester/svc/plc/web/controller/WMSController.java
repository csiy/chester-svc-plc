package com.chester.svc.plc.web.controller;

import com.chester.auth.client.core.UserTokenHolder;
import com.chester.svc.plc.mongodb.model.Material;
import com.chester.svc.plc.mongodb.model.Mission;
import com.chester.svc.plc.mongodb.repository.MaterialRepository;
import com.chester.svc.plc.mongodb.repository.MissionRepository;
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
    private MissionRepository missionRepository;
    @Resource
    private MaterialRepository materialRepository;

    @PostMapping
    public Boolean add(@RequestBody Mission wms) {
        Material material = materialRepository.getMaterial(wms.getSKU(), wms.getSOREFERENCE3());
        Assert.notNull(material, "物料号或AO工序号不存在");
        wms.setLineNumber(1);
        wms.setCount(Integer.parseInt(wms.getQty_each()));
        wms.initMaterial(material);
        missionRepository.addMission(wms, UserTokenHolder.getUserId());
        return true;
    }
}
