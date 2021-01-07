package com.chester.svc.plc.mongodb.repository;

import com.chester.cloud.support.mongodb.AccessUtils;
import com.chester.data.mongo.MongoPageQuery;
import com.chester.svc.plc.mongodb.config.Constant;
import com.chester.svc.plc.mongodb.model.Job;
import com.chester.svc.plc.mongodb.model.Logs;
import com.chester.svc.plc.mongodb.model.Machine;
import com.chester.svc.plc.mongodb.config.MongoCollections;
import com.chester.svc.plc.mqtt.MqttSender;
import com.chester.svc.plc.mqtt.payload.DiscPayload;
import com.chester.svc.plc.mqtt.payload.SwitchPayload;
import com.chester.svc.plc.web.model.req.ReqPageMachine;
import com.chester.svc.sys.db.repository.UserRepository;
import com.chester.util.json.JSON;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.conversions.Bson;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class MachineRepository {
    @Resource
    private MongoDatabase db;
    private MongoCollection<Machine> coll;
    @Resource
    private UserRepository userRepository;
    @Resource
    private LogsRepository logsRepository;
    @Resource
    private JobRepository jobRepository;
    @Resource
    private MqttSender mqttSender;
    @Resource
    private MissionRepository missionRepository;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    private static final Logs.LogsType LOG_TYPE = Logs.LogsType.MACHINE;


    @PostConstruct
    public void afterPropertiesSet() {
        this.coll = db.getCollection(MongoCollections.machine, Machine.class);
    }

    public void addMachine(String clientName){
        Machine machine = this.coll.find(Filters.and(Filters.eq("_id",clientName),Filters.eq(Constant.isDeleted, Boolean.FALSE))).first();
        if(machine==null){
            machine = new Machine();
            AccessUtils.prepareEntityBeforeInstall(machine, 1L, "系统");
            machine.setMachineId(clientName);
            machine.setRuntimeJobSetStatus(0);
            machine.setRuntimeJobStatus(0);
            machine.setLinkState(true);
            machine.setRunState(false);
            machine.setVersion(1);
            machine.setLostThreshold(System.currentTimeMillis()+1000*60);
            this.coll.insertOne(machine);
            logsRepository.addLogs(LOG_TYPE,"创建",machine);
        }
    }

    public Machine getMachine(String machineId){
        return this.coll.find(Filters.and(Filters.eq("_id",machineId),Filters.eq(Constant.isDeleted, Boolean.FALSE))).first();
    }

    public void deleteMachine(String machineId,Integer version, Long updatedBy){
        Bson filter = Filters.and(Filters.eq(Constant._id, machineId), Filters.eq(Constant.version, version));
        Machine before = getMachine(machineId);
        UpdateResult result = this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                Updates.set(Constant.isDeleted, true),
                Updates.inc(Constant.version,1)
        ));
        if(result.getModifiedCount()==0){
            throw new IllegalArgumentException("找不到数据或者数据已被修改");
        }
        Machine after = this.coll.find(Filters.eq(Constant._id, machineId)).first();
        logsRepository.addLogs(LOG_TYPE,"删除",before,after);
    }

    public void updateMachine(Machine machine, Long updatedBy){
        Bson filter = Filters.and(
                Filters.eq(Constant._id, machine.getMachineId()),
                Filters.eq(Constant.version, machine.getVersion()),
                Filters.eq(Constant.isDeleted, Boolean.FALSE)
        );
        Machine before = getMachine(machine.getMachineId());
        UpdateResult result = this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                Updates.set("address", machine.getAddress()),
                Updates.set("diskList", machine.getDiskList()),
                Updates.inc(Constant.version,1)
        ));
        if(result.getModifiedCount()==0){
            throw new IllegalArgumentException("找不到数据或者数据已被修改");
        }
        Machine after = this.coll.find(Filters.eq(Constant._id, machine.getMachineId())).first();
        logsRepository.addLogs(LOG_TYPE,"修改盘列表和地址",before,after);
    }

    /**
     * 修改当前盘号
     * @param machine
     * @param updatedBy
     */
    public void updateRuntimeDish(Machine machine, Long updatedBy){
        Machine before = getMachine(machine.getMachineId());
        if(before!=null){
            if(before.getRunState()&&before.getRuntimeJob()!=null&&before.getRuntimeJobStatus()==1){
                throw new IllegalArgumentException("当前有任务正在执行，不能修改当前盘状态");
            }
            Bson filter = Filters.and(
                    Filters.eq(Constant._id, machine.getMachineId()),
                    Filters.eq(Constant.version, machine.getVersion()),
                    Filters.eq(Constant.isDeleted, Boolean.FALSE)
            );
            UpdateResult result = this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                    Updates.set("disk", machine.getDisk()),
                    Updates.inc(Constant.version,1)
            ));
            if(result.getModifiedCount()==0){
                throw new IllegalArgumentException("找不到数据或者数据已被修改");
            }
            Machine after = this.coll.find(Filters.eq(Constant._id, machine.getMachineId())).first();
            logsRepository.addLogs(LOG_TYPE,"修改当前盘号",before,after);
        }
    }

    public PageResult<Machine> machinePageResult(ReqPageMachine query, Pagination pagination) {
        Bson sort = Sorts.descending(Constant.createdOn);
        Bson filter = Filters.eq(Constant.isDeleted, Boolean.FALSE);
        if (query.getAddress() != null) {
            filter = Filters.and(Filters.regex("address", query.getAddress()), filter);
        }
        return MongoPageQuery.builder(coll, Machine.class).sort(sort).page(pagination).filter(filter).execute();
    }

    public List<Machine> findAliveMachines(){
        Bson filter = Filters.and(
                Filters.exists("disk"),
                Filters.eq(Constant.isDeleted, Boolean.FALSE),
                Filters.eq("linkState", true)
        );
        return this.coll.find(filter).into(new ArrayList<>());
    }

    public List<Machine> findAllMachines(){
        Bson filter = Filters.and(
                Filters.eq(Constant.isDeleted, Boolean.FALSE)
        );
        return this.coll.find(filter).sort(Sorts.descending("linkState")).into(new ArrayList<>());
    }

    public List<Machine> findUnLinked(){
        Bson filter = Filters.and(
                Filters.eq(Constant.isDeleted, Boolean.FALSE),
                Filters.lt("lostThreshold",System.currentTimeMillis()),
                Filters.eq("linkState", true)
        );
        return this.coll.find(filter).into(new ArrayList<>());
    }

    public void linked(String machineId){
        this.coll.updateOne(Filters.eq(Constant._id, machineId), AccessUtils.prepareUpdates(1L, "系统",
                Updates.set("linkState", true),
                Updates.set("lostThreshold",System.currentTimeMillis()+1000*60)
        ));
    }

    public void unLinked(String machineId){
        this.coll.updateOne(Filters.eq(Constant._id, machineId), AccessUtils.prepareUpdates(1L, "系统",
                Updates.set("linkState", false),
                Updates.set("runState",false)
        ));
    }

    public void updateMission(String missionId,Integer missionStatus) throws InterruptedException {
        if(missionStatus==2){
            String key = "m:f:"+missionId;
            String finishMission = stringRedisTemplate.opsForValue().getAndSet(key,missionId);
            if(finishMission!=null){
                return;
            }
        }
        Job job = jobRepository.getJob(missionId);
        if(job==null){
            return;
        }
        this.coll.updateOne(Filters.eq(Constant._id, job.getMachineId()), AccessUtils.prepareUpdates(1L, "系统",
                Updates.set("runtimeJobStatus",missionStatus)
        ));
        jobRepository.updateMission(missionId,missionStatus);
        missionRepository.updateMission(missionId,missionStatus);
        if(missionStatus==2){
            log.info("任务执行完毕 machineId：{}, jobId：{}",job.getMachineId(),missionId);
            Thread.sleep(3000);
            stopMachine(job.getMachineId());
            Job nextJob = jobRepository.getNextJob(getMachine(job.getMachineId()));
            if(nextJob!=null&&nextJob.getMaterial().getMaterialCode().equals(job.getMaterial().getMaterialCode())){
                log.info("自动执行下一个任务 machineId：{}, jobId：{}",job.getMachineId(),nextJob.getJobId());
                Machine machine = getMachine(job.getMachineId());
                Thread.sleep(3000);
                setDish(machine,nextJob);
                Thread.sleep(3000);
                runMachine(machine.getMachineId());
            }
        }else if(missionStatus==3){
            log.info("任务异常 machineId：{}, jobId：{}",job.getMachineId(),missionId);
            Thread.sleep(3000);
            stopMachine(job.getMachineId());
        }
    }

    public void runMachine(String machineId){
        log.info("开启机器");
        mqttSender.sendMessage(machineId, new SwitchPayload("open"));
        updateRunStateConfirm(machineId,true);
    }

    public void stopMachine(String machineId){
        log.info("停止机器");
        mqttSender.sendMessage(machineId, new SwitchPayload("close"));
        updateRunStateConfirm(machineId,false);
    }

    public void setDish(Machine machine,Job job){
        this.coll.updateOne(Filters.eq(Constant._id, machine.getMachineId()), AccessUtils.prepareUpdates(1L, "系统",
                Updates.set("runtimeJob", job.getJobId()),
                Updates.set("runtimeMaterialCode",job.getMaterial().getMaterialCode()),
                Updates.set("runtimeJobSetStatus",1)
        ));
        jobRepository.setJobMachine(job.getJobId(),machine.getMachineId());
        DiscPayload.SetDiscList setDiscList = new DiscPayload.SetDiscList();
        setDiscList.setDiscNo(machine.getDiskList().indexOf(machine.getDisk())+1);
        log.info("set disc diskList:{},disk:{},discNo:{}", JSON.stringify(machine.getDiskList()),machine.getDisk(),setDiscList.getDiscNo());
        setDiscList.setTotalB(job.getMission().getCount());
        setDiscList.setTotalOneB(job.getMaterial().getQuantity());
        mqttSender.sendMessage(machine.getMachineId(), new DiscPayload(job.getJobId(),setDiscList));
        updateSetMissionConfirm(machine.getMachineId());
    }

    public void updateRunStateConfirm(String machineId,Boolean runState){
        Bson filter = Filters.and(
                Filters.eq(Constant._id, machineId),
                Filters.eq(Constant.isDeleted, Boolean.FALSE)
        );
        this.coll.updateOne(filter, AccessUtils.prepareUpdates(1L, "系统",
                Updates.set("runState",runState),
                Updates.set("runtimeJobSetStatus",runState?2:0)
        ));
    }

    public void updateSetMissionConfirm(String machineId){
        Bson filter = Filters.and(
                Filters.eq(Constant._id, machineId),
                Filters.eq(Constant.isDeleted, Boolean.FALSE)
        );
        this.coll.updateOne(filter, AccessUtils.prepareUpdates(1L, "系统",
                Updates.set("runtimeJobSetStatus",2)
        ));
    }
}
