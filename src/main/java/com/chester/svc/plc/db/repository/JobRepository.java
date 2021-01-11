package com.chester.svc.plc.db.repository;

import com.chester.svc.plc.db.model.Job;
import com.chester.svc.plc.db.model.Machine;
import com.chester.svc.sys.db.repository.UserRepository;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Repository
public class JobRepository {
    @Resource
    private UserRepository userRepository;
    @Resource
    private MissionRepository missionRepository;

    public void addJobs(List<Job> jobs){

    }

    public void deleteJob(String jobId,Integer version, Long updatedBy){

    }

    /**
     * 未排程工单
     */
    public PageResult<Job> jobUnSchedulerPageResult(List<Machine> list,Pagination pagination) {
        return null;
    }

    public Job getJob(String jobId){
        return null;
    }

    /**
     * 获取已排程
     * @param machine
     * @return
     */
    public List<Job> getJobList(Machine machine){
        return null;
    }

    public void setJobMachine(Long jobId,Long machineId){
    }

    public Job getNextJob(Machine machine){
        List<Job> list = getJobList(machine);
        if(list.size()>0){
            return list.get(0);
        }
        return null;
    }

    public void updateMission(Long jobId,Integer missionStatus){

    }
}
