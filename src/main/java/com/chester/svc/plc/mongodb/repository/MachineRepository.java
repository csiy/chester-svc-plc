package com.chester.svc.plc.mongodb.repository;

import com.chester.cloud.support.mongodb.AccessUtils;
import com.chester.data.mongo.MongoPageQuery;
import com.chester.svc.plc.mongodb.config.Constant;
import com.chester.svc.plc.mongodb.model.Logs;
import com.chester.svc.plc.mongodb.model.Machine;
import com.chester.svc.plc.mongodb.config.MongoCollections;
import com.chester.svc.plc.web.model.req.ReqPageMachine;
import com.chester.svc.sys.mongodb.repository.UserRepository;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Repository
public class MachineRepository {
    @Resource
    private MongoDatabase db;
    private MongoCollection<Machine> coll;
    @Resource
    private UserRepository userRepository;
    @Resource
    private LogsRepository logsRepository;
    private static final Logs.LogsType LOG_TYPE = Logs.LogsType.MACHINE;


    @PostConstruct
    public void afterPropertiesSet() {
        this.coll = db.getCollection(MongoCollections.machine, Machine.class);
    }

    public void addMachine(Machine machine, Long createdBy){
        AccessUtils.prepareEntityBeforeInstall(machine, createdBy, userRepository.getUserName(createdBy));
        machine.setMachineId(ObjectId.get().toHexString());
        machine.setVersion(1);
        this.coll.insertOne(machine);
        logsRepository.addLogs(LOG_TYPE,"创建",machine);
    }

    public void deleteMachine(String machineId,Integer version, Long updatedBy){
        Bson filter = Filters.and(Filters.eq(Constant._id, machineId), Filters.eq(Constant.version, version));
        Machine before = this.coll.find(Filters.eq(Constant._id, machineId)).first();
        UpdateResult result = this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                Updates.set(Constant.isDeleted, true)
        ));
        if(result.getModifiedCount()==0){
            throw new IllegalArgumentException("找不到数据或者数据已被修改");
        }
        Machine after = this.coll.find(Filters.eq(Constant._id, machineId)).first();
        logsRepository.addLogs(LOG_TYPE,"删除",before,after);
    }

    public void updateMachine(Machine machine, Long updatedBy){
        Bson filter = Filters.and(Filters.eq(Constant._id, machine.getMachineId()), Filters.eq(Constant.version, machine.getVersion()));
        Machine before = this.coll.find(Filters.eq(Constant._id, machine.getMachineId())).first();
        UpdateResult result = this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                Updates.set("address", machine.getAddress()),
                Updates.set("machineDishList", machine.getMachineDishList())
        ));
        if(result.getModifiedCount()==0){
            throw new IllegalArgumentException("找不到数据或者数据已被修改");
        }
        Machine after = this.coll.find(Filters.eq(Constant._id, machine.getMachineId())).first();
        logsRepository.addLogs(LOG_TYPE,"修改",before,after);
    }

    public PageResult<Machine> machinePageResult(ReqPageMachine query, Pagination pagination) {
        Bson sort = Sorts.descending(Constant.createdOn);
        Bson filter = Filters.eq(Constant.isDeleted, Boolean.FALSE);
        if (query.getAddress() != null) {
            filter = Filters.and(Filters.regex("address", query.getAddress()), filter);
        }
        return MongoPageQuery.builder(coll, Machine.class).sort(sort).page(pagination).filter(filter).execute();
    }

}
