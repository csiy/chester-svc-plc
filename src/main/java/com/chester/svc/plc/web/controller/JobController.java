package com.chester.svc.plc.web.controller;

import com.chester.svc.plc.db.model.Mission;
import com.chester.svc.plc.db.repository.MissionRepository;
import com.chester.svc.sys.annotation.Roles;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/plc/jobs")
public class JobController {
    @Resource
    private MissionRepository missionRepository;

    @GetMapping("/unScheduler")
    @Roles(value = "admin,operator", remark = "查询未排程工单")
    public PageResult<Mission> getMaterial(Pagination pagination) {
        return null;
    }

    @GetMapping("/{jobId}")
    @Roles(value = "admin,operator", remark = "获取任务")
    public Mission getJob(@PathVariable("jobId") String jobId) {
        return null;
    }

    @GetMapping("/jobs/{machineId}")
    @Roles(value = "admin,operator", remark = "获取任务列表")
    public List<Mission> getJobs(@PathVariable("machineId") Long machineId) {
        return null;
    }

    @DeleteMapping("/{jobId}/{version}")
    @Roles(value = "admin,operator", remark = "删除排程")
    public void deleteMaterial(@PathVariable("jobId") String jobId, @PathVariable("version") Integer version) {
    }

}
