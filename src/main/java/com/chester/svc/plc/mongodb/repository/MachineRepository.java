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
import com.chester.svc.sys.mongodb.repository.UserRepository;
import com.chester.util.coll.Lists;
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
    private static final Logs.LogsType LOG_TYPE = Logs.LogsType.MACHINE;


    @PostConstruct
    public void afterPropertiesSet() {
        this.coll = db.getCollection(MongoCollections.machine, Machine.class);
    }

    public void addMachine(String clientName){
        Machine machine = this.coll.find(Filters.eq("_id",clientName)).first();
        if(machine==null){
            machine = new Machine();
            AccessUtils.prepareEntityBeforeInstall(machine, 1L, "系统");
            machine.setMachineId(clientName);
            machine.setMachineDishList(new ArrayList<>());
            machine.setRuntimeDishNumber(-1);
            machine.setRuntimeDish(null);
            machine.setLinkState(true);
            machine.setRunState(false);
            machine.setJobs(new ArrayList<>());
            machine.setVersion(1);
            machine.setLostThreshold(System.currentTimeMillis()+1000*60);
            this.coll.insertOne(machine);
            logsRepository.addLogs(LOG_TYPE,"创建",machine);
        }
    }

    public void deleteMachine(String machineId,Integer version, Long updatedBy){
        Bson filter = Filters.and(Filters.eq(Constant._id, machineId), Filters.eq(Constant.version, version));
        Machine before = this.coll.find(Filters.eq(Constant._id, machineId)).first();
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
        Machine before = this.coll.find(Filters.eq(Constant._id, machine.getMachineId())).first();
        UpdateResult result = this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                Updates.set("address", machine.getAddress()),
                Updates.set("machineDishList", machine.getMachineDishList()),
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
        Bson filter = Filters.and(
                Filters.eq(Constant._id, machine.getMachineId()),
                Filters.eq(Constant.version, machine.getVersion()),
                Filters.eq(Constant.isDeleted, Boolean.FALSE)
        );
        Machine before = this.coll.find(Filters.eq(Constant._id, machine.getMachineId())).first();
        UpdateResult result = this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                Updates.set("runtimeDishNumber", machine.getRuntimeDishNumber()),
                Updates.set("jobs", new ArrayList<>()),
                Updates.set("runtimeDish", machine.getMachineDishList().get(machine.getRuntimeDishNumber())),
                Updates.inc(Constant.version,1)
        ));
        if(result.getModifiedCount()==0){
            throw new IllegalArgumentException("找不到数据或者数据已被修改");
        }
        jobRepository.releaseScheduler(before.getJobs());
        Machine after = this.coll.find(Filters.eq(Constant._id, machine.getMachineId())).first();
        logsRepository.addLogs(LOG_TYPE,"修改当前盘号",before,after);
    }

    public PageResult<Machine> machinePageResult(ReqPageMachine query, Pagination pagination) {
        Bson sort = Sorts.descending(Constant.createdOn);
        Bson filter = Filters.eq(Constant.isDeleted, Boolean.FALSE);
        if (query.getAddress() != null) {
            filter = Filters.and(Filters.regex("address", query.getAddress()), filter);
        }
        return MongoPageQuery.builder(coll, Machine.class).sort(sort).page(pagination).filter(filter).execute();
    }

    public List<Machine> findMachines(){
        Bson filter = Filters.and(
                Filters.ne("runtimeDishNumber",-1),
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

    public void pushJobs(String machineId,List<String> jobs){
        if(!Lists.isEmpty(jobs)){
            this.coll.updateOne(Filters.eq(Constant._id, machineId), AccessUtils.prepareUpdates(1L, "系统",
                    Updates.addEachToSet("jobs", jobs)
            ));
            Machine machine = getMachine(machineId);
            if(machine.getRuntimeJob()==null){
                this.coll.updateOne(Filters.eq(Constant._id, machineId), AccessUtils.prepareUpdates(1L, "系统",
                        Updates.set("runtimeJob", jobs.get(0)),
                        Updates.pull("jobs", jobs.get(0)),
                        Updates.set("runState",false)
                ));
            }
        }
    }

    public void linked(String machineId){
        this.coll.updateOne(Filters.eq(Constant._id, machineId), AccessUtils.prepareUpdates(1L, "系统",
                Updates.set("linkState", true),
                Updates.set("lostThreshold",System.currentTimeMillis()+1000*60)
        ));
    }

    public void unLinked(String machineId){
        log.info("链接中断释放排程 machineId：{}",machineId);
        Machine before = this.coll.find(Filters.eq(Constant._id, machineId)).first();
        jobRepository.releaseScheduler(before.getJobs());
        this.coll.updateOne(Filters.eq(Constant._id, machineId), AccessUtils.prepareUpdates(1L, "系统",
                Updates.set("linkState", false),
                Updates.set("jobs", new ArrayList<>()),
                Updates.set("runState",false)
        ));
    }

    public void nextJob(String machineId,String jobId,Integer version, Long updatedBy){
        Bson filter = Filters.and(
                Filters.eq(Constant._id, machineId),
                Filters.eq(Constant.version, version),
                Filters.eq(Constant.isDeleted, Boolean.FALSE)
        );
        this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                Updates.set("runtimeJob", jobId),
                Updates.pull("jobs", jobId),
                Updates.set("runState",false),
                Updates.inc(Constant.version,1)
        ));
    }

    public Machine getMachine(String machineId){
        return this.coll.find(Filters.eq("_id",machineId)).first();
    }

    public void runMachine(String machineId,Integer version, Long updatedBy){
        Machine before = this.coll.find(Filters.eq(Constant._id, machineId)).first();
        if(before.getRuntimeJob()!=null){
            Bson filter = Filters.and(
                    Filters.eq(Constant._id, machineId),
                    Filters.eq(Constant.version, version),
                    Filters.eq(Constant.isDeleted, Boolean.FALSE)
            );
            UpdateResult result = this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                    Updates.set("runState",true)
            ));
            if(result.getModifiedCount()==0){
                throw new IllegalArgumentException("找不到数据或者数据已被修改");
            }
            Machine after = this.coll.find(Filters.eq(Constant._id, machineId)).first();
            logsRepository.addLogs(LOG_TYPE,"开启机器",before,after);
            Job job = jobRepository.getJob(before.getRuntimeJob());
            DiscPayload.SetDiscList setDiscList = new DiscPayload.SetDiscList();
            setDiscList.setDiscNo(before.getRuntimeDishNumber());
            setDiscList.setTotalB(job.getMission().getCount());
            setDiscList.setTotalOneB(job.getMaterial().getQuantity());
            mqttSender.sendMessage(machineId, new DiscPayload(setDiscList));
            mqttSender.sendMessage(machineId, new SwitchPayload("open"));
        }
    }

    public void stopMachine(String machineId,Integer version, Long updatedBy){
        Bson filter = Filters.and(
                Filters.eq(Constant._id, machineId),
                Filters.eq(Constant.version, version),
                Filters.eq(Constant.isDeleted, Boolean.FALSE)
        );
        Machine before = this.coll.find(Filters.eq(Constant._id, machineId)).first();
        UpdateResult result = this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                Updates.set("runtimeJob", null),
                Updates.set("runState",false)
        ));
        if(result.getModifiedCount()==0){
            throw new IllegalArgumentException("找不到数据或者数据已被修改");
        }
        Machine machine = getMachine(machineId);
        if(!Lists.isEmpty(machine.getJobs())){
            this.coll.updateOne(Filters.eq(Constant._id, machineId), AccessUtils.prepareUpdates(1L, "系统",
                    Updates.set("runtimeJob", machine.getJobs().get(0)),
                    Updates.pull("jobs", machine.getJobs().get(0)),
                    Updates.set("runState",false)
            ));
        }
        Machine after = this.coll.find(Filters.eq(Constant._id, machineId)).first();
        logsRepository.addLogs(LOG_TYPE,"关闭机器",before,after);
        mqttSender.sendMessage(machineId, new SwitchPayload("close"));
    }

    public void reSort(String machineId,List<String> jobs,Integer version, Long updatedBy){
        if(!Lists.isEmpty(jobs)){
            Bson filter = Filters.and(
                    Filters.eq(Constant._id, machineId),
                    Filters.eq(Constant.version, version),
                    Filters.eq(Constant.isDeleted, Boolean.FALSE)
            );
            UpdateResult result = this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                    Updates.set("jobs", jobs),
                    Updates.inc(Constant.version,1)
            ));
            if(result.getModifiedCount()==0){
                throw new IllegalArgumentException("找不到数据或者数据已被修改");
            }
        }
    }
}
