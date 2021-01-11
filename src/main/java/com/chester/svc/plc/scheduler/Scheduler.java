package com.chester.svc.plc.scheduler;

import com.chester.svc.plc.db.model.Job;
import com.chester.svc.plc.db.model.Machine;
import com.chester.svc.plc.db.model.Material;
import com.chester.svc.plc.db.model.Mission;
import com.chester.svc.plc.db.repository.JobRepository;
import com.chester.svc.plc.db.repository.MachineRepository;
import com.chester.svc.plc.db.repository.MaterialRepository;
import com.chester.svc.plc.db.repository.MissionRepository;
import com.chester.util.coll.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
@EnableScheduling
public class Scheduler {

    @Resource
    private MachineRepository machineRepository;
    @Resource
    private MissionRepository missionRepository;
    @Resource
    private MaterialRepository materialRepository;
    @Resource
    private JobRepository jobRepository;

    //每分钟执行计划单转化任务
    @Scheduled(fixedRate = 5000)
    public void schedulerTransform() {
        List<Mission> list = missionRepository.findUnTransformMission();
        if(!Lists.isEmpty(list)){
            List<Job> jobs = Lists.map(list,v->{
                Job job = new Job();
                Material material = materialRepository.getMaterial(v.getMaterialCode(),v.getAoCode());
                job.setJobId(v.getMissionId());
                job.setMaterial(material);
                job.setMission(v);
                job.setJobStatus(0);
                job.setIsFinish(false);
                job.setIsError(false);
                return job;
            });
            List<Job> successJob = Lists.filter(jobs,v->v.getMaterial()!=null);
            List<Long> errorIds = Lists.map(Lists.filter(jobs,v->v.getMaterial()==null),v->v.getMission().getMissionId());
            List<Long> successIds = Lists.map(successJob,v->v.getMission().getMissionId());
            if(!Lists.isEmpty(errorIds)){
                missionRepository.transformError(errorIds);
            }
            if(!Lists.isEmpty(successIds)){
                missionRepository.transformSuccess(successIds);
                jobRepository.addJobs(successJob);
            }
        }
    }

    //每10秒执行超时处理
    @Scheduled(fixedRate = 5000)
    public void lostConnect(){
        List<Machine> list = machineRepository.findUnLinked();
        Lists.each(list,v->{
            machineRepository.unLinked(v.getKey());
        });
    }

}
