package com.chester.svc.plc.mongodb.repository;

import com.chester.cloud.support.mongodb.AccessUtils;
import com.chester.data.mongo.MongoPageQuery;
import com.chester.svc.plc.mongodb.config.Constant;
import com.chester.svc.plc.mongodb.model.Machine;
import com.chester.svc.plc.mongodb.config.MongoCollections;
import com.chester.svc.plc.mongodb.model.Mission;
import com.chester.svc.plc.mqtt.MqttSender;
import com.chester.svc.plc.mqtt.payload.DiscPayload;
import com.chester.svc.plc.mqtt.payload.SwitchPayload;
import com.chester.svc.plc.web.model.req.ReqPageMachine;
import com.chester.svc.sys.mongodb.repository.UserRepository;
import com.chester.util.coll.Lists;
import com.chester.util.json.JSON;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import lombok.extern.slf4j.Slf4j;
import org.bson.conversions.Bson;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

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
    private MqttSender mqttSender;
    @Resource
    private MissionRepository missionRepository;
    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @PostConstruct
    public void afterPropertiesSet() {
        this.coll = db.getCollection(MongoCollections.machine, Machine.class);
        addMachine("M00001");
    }

    public void addMachine(String clientName) {
        Machine machine = this.coll.find(Filters.and(Filters.eq(Constant._id, clientName), Filters.eq(Constant.isDeleted, Boolean.FALSE))).first();
        if (machine == null) {
            machine = new Machine();
            AccessUtils.prepareEntityBeforeInstall(machine, 1L, "系统");
            machine.setMachineId(clientName);
            machine.setLinkState(true);
            machine.setLostThreshold(System.currentTimeMillis() + 1000 * 60);
            this.coll.insertOne(machine);
        }
    }

    public Machine getMachine(String machineId) {
        return this.coll.find(Filters.and(Filters.eq(Constant._id, machineId), Filters.eq(Constant.isDeleted, Boolean.FALSE))).first();
    }


    public void updateMachine(Machine machine, Long updatedBy) {
        Machine _machine = getMachine(machine.getMachineId());
        Assert.isTrue(Lists.filter(_machine.getDisks(), v -> v.getMissionId() != null).size() == 0, "当前有正在执行的任务，不能修改");
        Bson filter = Filters.eq(Constant._id, machine.getMachineId());
        this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                Updates.set(Constant.address, machine.getAddress()),
                Updates.set(Constant.disks, machine.getDisks())
        ));
    }

    public void updateMachineDisk(String machineId, List<Machine.Disk> disks) {
        Bson filter = Filters.eq(Constant._id, machineId);
        this.coll.updateOne(filter, AccessUtils.prepareUpdates(1L, "系统",
                Updates.set(Constant.disks, disks)
        ));
    }

    public PageResult<Machine> machinePageResult(ReqPageMachine query, Pagination pagination) {
        Bson sort = Sorts.descending(Constant.createdOn);
        Bson filter = Filters.eq(Constant.isDeleted, Boolean.FALSE);
        if (query.getAddress() != null) {
            filter = Filters.and(Filters.regex(Constant.address, query.getAddress()), filter);
        }
        return MongoPageQuery.builder(coll, Machine.class).sort(sort).page(pagination).filter(filter).execute();
    }

    public List<Machine> findAllMachines() {
        Bson filter = Filters.and(
                Filters.eq(Constant.isDeleted, Boolean.FALSE)
        );
        return this.coll.find(filter).sort(Sorts.descending(Constant.linkState)).into(new ArrayList<>());
    }

    public List<Machine> findLinked() {
        Bson filter = Filters.and(
                Filters.eq(Constant.isDeleted, Boolean.FALSE),
                Filters.gt(Constant.lostThreshold, System.currentTimeMillis()),
                Filters.eq(Constant.linkState, Boolean.TRUE)
        );
        return this.coll.find(filter).into(new ArrayList<>());
    }

    public List<Machine> findUnLinked(){
        Bson filter = Filters.and(
                Filters.eq(Constant.isDeleted, Boolean.FALSE),
                Filters.lt(Constant.lostThreshold, System.currentTimeMillis()),
                Filters.eq(Constant.linkState, Boolean.TRUE)
        );
        return this.coll.find(filter).into(new ArrayList<>());
    }

    public void linked(String machineId) {
        this.coll.updateOne(Filters.eq(Constant._id, machineId), AccessUtils.prepareUpdates(1L, "系统",
                Updates.set(Constant.linkState, Boolean.TRUE),
                Updates.set(Constant.lostThreshold, System.currentTimeMillis() + 1000 * 60)
        ));
    }

    public void unLinked(String machineId) {
        List<Machine.Disk> disks = getMachine(machineId).getDisks();
        Lists.each(disks, v -> v.setMissionId(null));
        this.coll.updateOne(Filters.eq(Constant._id, machineId), AccessUtils.prepareUpdates(1L, "系统",
                Updates.set(Constant.linkState, Boolean.FALSE),
                Updates.set(Constant.disks, disks)
        ));
        missionRepository.updateMission(machineId);
    }

    public void updateMission(String machineId, String missionId, Integer discNo, Integer status) throws InterruptedException {
        if (status == 2) {
            String key = "m:f:" + missionId + ":n" + discNo;
            String finishMission = stringRedisTemplate.opsForValue().getAndSet(key, missionId);
            if (finishMission != null) {
                return;
            }
        }
        missionRepository.updateMission(missionId, status);
        if (status == 2) {
            stopMachine(machineId, discNo);
            Thread.sleep(3000);
            if (setNext(machineId, discNo)) {
                Thread.sleep(3000);
                runMachine(machineId, discNo);
            }
        } else if (status == 3) {
            stopMachine(machineId, discNo);
        }
    }

    public Boolean setNext(String machineId, Integer discNo) {
        List<Machine.Disk> disks = getMachine(machineId).getDisks();
        String disc = disks.get(discNo).getName();
        Mission next = missionRepository.getNext(machineId, disc);
        if (next != null) {
            disks.get(discNo).setMissionId(next.getMissionId());
            updateMachineDisk(machineId, disks);
            setDish(machineId, discNo, next.getQuantity(), next.getCount());
            return true;
        }
        return false;
    }

    public void runMachine(String machineId, Integer discNo) {
        mqttSender.sendMessage(machineId, new SwitchPayload(Constant.open, discNo));
    }

    public void stopMachine(String machineId, Integer discNo) {
        List<Machine.Disk> disks = getMachine(machineId).getDisks();
        disks.get(discNo).setMissionId(null);
        updateMachineDisk(machineId, disks);
        mqttSender.sendMessage(machineId, new SwitchPayload(Constant.close, discNo));
    }

    private void setDish(String machineId, Integer discNo, Integer quantity, Integer count) {
        DiscPayload.SetDiscList setDiscList = new DiscPayload.SetDiscList(discNo, quantity, count);
        mqttSender.sendMessage(machineId, new DiscPayload(machineId, setDiscList));
    }
}
