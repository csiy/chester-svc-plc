package com.chester.svc.plc.web.controller;

import com.chester.auth.client.annotation.Roles;
import com.chester.auth.client.core.UserTokenHolder;
import com.chester.svc.plc.mongodb.model.Job;
import com.chester.svc.plc.mongodb.model.Machine;
import com.chester.svc.plc.mongodb.repository.JobRepository;
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

    @GetMapping("/unScheduler")
    @Roles(value = "admin,operator", remark = "查询未排程工单")
    public PageResult<Job> getMaterial(Pagination pagination){
        return jobRepository.jobUnSchedulerPageResult(pagination);
    }

    @GetMapping("/{jobId}")
    @Roles(value = "admin,operator", remark = "获取任务")
    public Job getJob(@PathVariable("jobId") String jobId){
        return jobRepository.getJob(jobId);
    }

    @PostMapping("/jobs")
    @Roles(value = "admin,operator", remark = "获取任务列表")
    public List<Job> getJob(@RequestBody Machine machine){
        return jobRepository.getJobList(machine.getJobs());
    }

    @DeleteMapping("/{jobId}/{version}")
    @Roles(value = "admin,operator", remark = "删除排程")
    public void deleteMaterial(@PathVariable("jobId") String jobId, @PathVariable("version")Integer version) {
        jobRepository.deleteJob(jobId,version, UserTokenHolder.getUserId());
    }

    @PutMapping
    @Roles(value = "admin,operator", remark = "修复排程")
    public void putJob(@RequestBody Job job) {
        jobRepository.updateJob(job,UserTokenHolder.getUserId());
    }
}
