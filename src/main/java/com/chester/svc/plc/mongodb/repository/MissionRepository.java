package com.chester.svc.plc.mongodb.repository;

import com.chester.cloud.support.mongodb.AccessUtils;
import com.chester.data.mongo.MongoPageQuery;
import com.chester.svc.plc.mongodb.config.Constant;
import com.chester.svc.plc.mongodb.model.Logs;
import com.chester.svc.plc.mongodb.model.Material;
import com.chester.svc.plc.mongodb.model.Mission;
import com.chester.svc.plc.mongodb.config.MongoCollections;
import com.chester.svc.plc.web.model.req.ReqPageMaterial;
import com.chester.svc.plc.web.model.req.ReqPageMission;
import com.chester.svc.sys.mongodb.repository.UserRepository;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Repository
public class MissionRepository {
    @Resource
    private MongoDatabase db;
    private MongoCollection<Mission> coll;
    @Resource
    private UserRepository userRepository;
    @Resource
    private SerialRepository serialRepository;
    @Resource
    private LogsRepository logsRepository;
    private static final Logs.LogsType LOG_TYPE = Logs.LogsType.TASK;

    @PostConstruct
    public void afterPropertiesSet() {
        this.coll = db.getCollection(MongoCollections.mission, Mission.class);
    }

    public void addMission(Mission mission,Long createdBy){
        AccessUtils.prepareEntityBeforeInstall(mission, createdBy, userRepository.getUserName(createdBy));
        mission.setMissionId(ObjectId.get().toHexString());
        mission.setSerialNumber(serialRepository.serialNumber());
        mission.setBatchNumber(serialRepository.batchNumber());
        mission.setVersion(1);
        this.coll.insertOne(mission);
        logsRepository.addLogs(LOG_TYPE,"创建",mission);
    }

    public void addMission(Mission mission,Long createdBy,Integer batchNumber){
        AccessUtils.prepareEntityBeforeInstall(mission, createdBy, userRepository.getUserName(createdBy));
        mission.setMissionId(ObjectId.get().toHexString());
        mission.setSerialNumber(serialRepository.serialNumber());
        mission.setBatchNumber(batchNumber);
        this.coll.insertOne(mission);
        logsRepository.addLogs(LOG_TYPE,"创建",mission);
    }

    public void deleteMission(String missionId,Integer version, Long updatedBy){
        Bson filter = Filters.and(Filters.eq(Constant._id, missionId), Filters.eq(Constant.version, version));
        this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                Updates.set(Constant.isDeleted, true)
        ));
    }

    public void updateMission(Mission mission, Long updatedBy){
        Bson filter = Filters.and(Filters.eq(Constant._id, mission.getMissionId()), Filters.eq(Constant.version, mission.getVersion()));
        this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                Updates.set(Constant.isDeleted, true)
        ));
    }

    public PageResult<Mission> missionPageResult(ReqPageMission query, Pagination pagination) {
        Bson sort = Sorts.descending(Constant.createdOn);
        Bson filter = Filters.eq(Constant.isDeleted, Boolean.FALSE);
//        if (query.getName() != null) {
//            filter = Filters.and(Filters.regex(Constant.name, query.getName()), filter);
//        }
//        if (query.getPhone() != null) {
//            filter = Filters.and(Filters.eq(Constant.phone, query.getPhone()), filter);
//        }
//        if (query.getRole() != null) {
//            filter = Filters.and(Filters.eq(Constant.roles, query.getRole()), filter);
//        }
        return MongoPageQuery.builder(coll, Mission.class).sort(sort).page(pagination).filter(filter).execute();
    }
}
