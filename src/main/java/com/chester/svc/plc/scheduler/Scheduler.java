package com.chester.svc.plc.scheduler;

import com.chester.svc.plc.mongodb.model.Job;
import com.chester.svc.plc.mongodb.model.Machine;
import com.chester.svc.plc.mongodb.model.Material;
import com.chester.svc.plc.mongodb.model.Mission;
import com.chester.svc.plc.mongodb.repository.JobRepository;
import com.chester.svc.plc.mongodb.repository.MachineRepository;
import com.chester.svc.plc.mongodb.repository.MaterialRepository;
import com.chester.svc.plc.mongodb.repository.MissionRepository;
import com.chester.svc.plc.mqtt.MqttSender;
import com.chester.svc.sys.db.repository.UserRepository;
import com.chester.svc.sys.web.model.res.ResUser;
import com.chester.util.coll.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
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
    @Resource
    private UserRepository userRepository;
    @Resource
    private MqttSender mqttSender;

    //每分钟执行计划单转化任务
    @Scheduled(fixedRate = 5000)
    public void schedulerTransform() {
        List<Mission> list = missionRepository.findUnTransformMission();
        if(!Lists.isEmpty(list)){
            List<Job> jobs = Lists.map(list,v->{
                Job job = new Job();
                Material material = materialRepository.getMaterial(v.getMaterialCode(),v.getAoCode());
                ResUser resUser = userRepository.randomUser();
                job.setJobId(v.getMissionId());
                job.setMaterial(material);
                job.setMaterialId(material.getMaterialId());
                job.setMission(v);
                job.setMachineId("");
                job.setVersion(1);
                job.setJobStatus(0);
                job.setIsFinish(false);
                if(resUser!=null){
                    job.setWorkId(resUser.getUserId());
                    job.setWorkName(resUser.getName());
                }
                job.setErrorMessages(new ArrayList<>());
                job.setIsError(false);
                return job;
            });
            List<Job> successJob = Lists.filter(jobs,v->v.getMaterial()!=null);
            List<String> errorIds = Lists.map(Lists.filter(jobs,v->v.getMaterial()==null),v->v.getMission().getMissionId());
            List<String> successIds = Lists.map(successJob,v->v.getMission().getMissionId());
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
            machineRepository.unLinked(v.getMachineId());
        });
    }

    //每10秒执行超时处理
    @Scheduled(fixedRate = 5000)
    public void testConnect(){
        mqttSender.sendBeat();
    }

}
