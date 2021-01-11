package com.chester.svc.plc.web.controller;

import com.chester.svc.auth.client.annotation.Roles;
import com.chester.svc.auth.client.core.UserTokenHolder;
import com.chester.svc.plc.db.model.Job;
import com.chester.svc.plc.db.repository.JobRepository;
import com.chester.svc.plc.db.repository.MachineRepository;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/plc/jobs")
public class JobController {
    @Resource
    private JobRepository jobRepository;
    @Resource
    private MachineRepository machineRepository;

    @GetMapping("/unScheduler")
    @Roles(value = "admin,operator", remark = "查询未排程工单")
    public PageResult<Job> getMaterial(Pagination pagination){
        return jobRepository.jobUnSchedulerPageResult(machineRepository.findAliveMachines(),pagination);
    }

    @GetMapping("/{jobId}")
    @Roles(value = "admin,operator", remark = "获取任务")
    public Job getJob(@PathVariable("jobId") String jobId){
        return jobRepository.getJob(jobId);
    }

    @GetMapping("/jobs/{machineId}")
    @Roles(value = "admin,operator", remark = "获取任务列表")
    public List<Job> getJobs(@PathVariable("machineId") Long machineId){
        return jobRepository.getJobList(machineRepository.getMachine(machineId));
    }

    @DeleteMapping("/{jobId}/{version}")
    @Roles(value = "admin,operator", remark = "删除排程")
    public void deleteMaterial(@PathVariable("jobId") String jobId, @PathVariable("version")Integer version) {
        jobRepository.deleteJob(jobId,version, UserTokenHolder.getUserId());
    }

}
