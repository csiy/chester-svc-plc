package com.chester.svc.plc.scheduler;

import com.chester.cloud.support.mongodb.AccessUtils;
import com.chester.svc.plc.core.model.JobStatus;
import com.chester.svc.plc.mongodb.model.Job;
import com.chester.svc.plc.mongodb.model.Machine;
import com.chester.svc.plc.mongodb.model.Material;
import com.chester.svc.plc.mongodb.model.Mission;
import com.chester.svc.plc.mongodb.repository.JobRepository;
import com.chester.svc.plc.mongodb.repository.MachineRepository;
import com.chester.svc.plc.mongodb.repository.MaterialRepository;
import com.chester.svc.plc.mongodb.repository.MissionRepository;
import com.chester.svc.plc.mqtt.MqttSender;
import com.chester.svc.sys.mongodb.repository.UserRepository;
import com.chester.svc.sys.web.model.res.ResUser;
import com.chester.util.coll.Lists;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
                job.setMaterial(material);
                job.setMission(v);
                job.setVersion(1);
                job.setMachineId("");
                job.setJobStatus(JobStatus.CREATE);
                if(resUser!=null){
                    job.setWorkId(resUser.getUserId());
                    job.setWorkName(resUser.getName());
                }
                job.setErrorMessages(new ArrayList<>());
                job.setIsError(false);
                AccessUtils.prepareEntityBeforeInstall(job, 1L, "系统");
                job.setJobId(ObjectId.get().toHexString());
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

    private <E> List<List<E>> batchList(List<E> list, int batchSize){
        List<List<E>> itemMap = new ArrayList<>();
        itemMap.add(new ArrayList<E>());
        for(E e : list){
            List<E> batchList= itemMap.get(itemMap.size()-1);
            if(batchList.size() == batchSize){//当list满足批次数量，新建一个list存放后面的数据
                batchList = new ArrayList<E>();
                itemMap.add(batchList);
            }
            batchList.add(e);
        }
        return itemMap;
    }

    //每分钟执行排程任务
    @Scheduled(fixedRate = 5000)
    public void schedulerJobs() {
        List<Job> list = jobRepository.jobUnScheduler();
        if(!Lists.isEmpty(list)){
            List<Machine> machines = machineRepository.findMachines();
            if(!Lists.isEmpty(machines)){
                Map<String,List<Job>> jobMap = Lists.groupBy(list,v->v.getMaterial().getDish().toString()+v.getMaterial().getGears().toString());
                Map<String,List<Machine>> machineMap = Lists.groupBy(machines,v->v.getRuntimeDish().getDish().toString()+v.getRuntimeDish().getGears().toString());
                jobMap.forEach((k,v)->{
                    List<Machine> machineList = machineMap.get(k);
                    if(Lists.isEmpty(machineList)){
                        jobRepository.schedulerError(Lists.map(v, Job::getJobId));
                    }else{
                        List<List<Job>> lists = batchList(v,machineList.size());
                        for(int i=0;i<machineList.size();i++){
                            List<String> jobIds = Lists.map(lists.get(i), Job::getJobId);
                            machineRepository.pushJobs(machineList.get(i).getMachineId(),jobIds);
                            jobRepository.scheduler(jobIds,machineList.get(i).getMachineId());
                        }
                    }
                });
            }
        }
    }

    //每10秒执行超时处理
    @Scheduled(fixedRate = 500000)
    public void lostConnect(){
        List<Machine> list = machineRepository.findUnLinked();
        Lists.each(list,v->{
            machineRepository.unLinked(v.getMachineId());
        });
    }

    //每10秒执行超时处理
    @Scheduled(fixedRate = 10000)
    public void testConnect(){
        mqttSender.sendBeat();
    }

}
