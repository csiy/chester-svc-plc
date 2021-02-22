package com.chester.svc.plc.web.controller;

import com.chester.svc.plc.mongodb.model.Machine;
import com.chester.svc.plc.mongodb.model.Material;
import com.chester.svc.plc.mongodb.model.Mission;
import com.chester.svc.plc.mongodb.model.WM;
import com.chester.svc.plc.mongodb.repository.MachineRepository;
import com.chester.svc.plc.mongodb.repository.MaterialRepository;
import com.chester.svc.plc.mongodb.repository.MissionRepository;
import com.chester.svc.plc.web.model.req.Print;
import com.chester.svc.plc.web.model.res.Result;
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
    public Result<String> add(@RequestBody WM wms) {
        Material material = materialRepository.getMaterial(wms.getMaterialCode(), wms.getAoCode());
        try{
            Assert.notNull(wms.getWaveNo(), "请输入分配ID");
            Assert.notNull(wms.getVerify(), "请输入检字号");
            Assert.notNull(wms.getHeight(), "请输入包装袋尺寸");
            Assert.notNull(wms.getWidth(), "请输入包装袋尺寸");
            Assert.notNull(wms.getHighSpeed(), "请输入高速震动量");
            Assert.notNull(wms.getLowSpeed(), "请输入低速震动量");
            Assert.notNull(material, "物料号或AO工序号不存在");
        }catch (Exception e){
            return Result.error(e.getLocalizedMessage());
        }

        Mission mission = new Mission();
        mission.setLineNumber(1);
        mission.setCount(wms.getCount());
        mission.initMaterial(material);
        mission.setHeight(wms.getHeight());
        mission.setWidth(wms.getWidth());
        mission.setHighSpeed(wms.getHighSpeed());
        mission.setLowSpeed(wms.getLowSpeed());
        mission.setWaveNo(wms.getWaveNo());
        mission.setVerify(wms.getVerify());
        missionRepository.addMission(mission, 10000L);
        return Result.success("导入成功");
    }

    @ApiOperation("打印任务")
    @PostMapping("/print")
    public void print(@RequestBody Print print){
        missionRepository.print(print.getIds().toArray(new String[]{}));
    }

    @ApiOperation("获取任务")
    @PostMapping("/mission/{waveNo}")
    public Result<Mission> mission(@PathVariable("waveNo") String waveNo){
        return Result.success(missionRepository.getMissionByWave(waveNo));
    }
}
