package com.chester.svc.plc.mongodb.repository;

import com.chester.cloud.support.mongodb.AccessUtils;
import com.chester.data.mongo.MongoPageQuery;
import com.chester.svc.plc.mongodb.config.Constant;
import com.chester.svc.plc.mongodb.model.Logs;
import com.chester.svc.plc.mongodb.model.Mission;
import com.chester.svc.plc.mongodb.config.MongoCollections;
import com.chester.svc.plc.web.model.req.ReqPageMission;
import com.chester.svc.sys.mongodb.repository.UserRepository;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        this.coll.createIndex(Indexes.ascending("dateTime"));
    }

    public void addMission(Mission mission,Long createdBy){
        addMission(mission,createdBy,serialRepository.batchNumber());
    }

    public void addMission(Mission mission,Long createdBy,Integer batchNumber){
        AccessUtils.prepareEntityBeforeInstall(mission, createdBy, userRepository.getUserName(createdBy));
        mission.setSerialNumber(serialRepository.serialNumber());
        mission.setBatchNumber(batchNumber);
        mission.setVersion(1);
        mission.setMissionId(getMissionId(mission));
        mission.setTransform(1);
        if(mission.getDateTime()==null){
            mission.setDateTime(new Date());
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        mission.setDate(dateFormat.format(mission.getDateTime()));
        mission.setTime(timeFormat.format(mission.getDateTime()));
        this.coll.insertOne(mission);
        logsRepository.addLogs(LOG_TYPE,"创建",mission);
    }

    private String getMissionId(Mission mission){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        StringBuilder builder = new StringBuilder();
        builder.append(sdf.format(mission.getCreatedOn()));
        builder.append(mission.getBatchNumber());
        builder.append(mission.getSerialNumber());
        builder.append(mission.getLineNumber());
        return builder.toString();
    }

    public void deleteMission(String missionId,Integer version, Long updatedBy){
        Bson filter = Filters.and(
                Filters.eq(Constant._id, missionId),
                Filters.eq(Constant.version, version)
        );
        Mission before = this.coll.find(Filters.eq(Constant._id, missionId)).first();
        UpdateResult result = this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                Updates.set(Constant.isDeleted, true),
                Updates.inc(Constant.version,1)
        ));
        if(result.getModifiedCount()==0){
            throw new IllegalArgumentException("找不到数据或者数据已被修改");
        }
        Mission after = this.coll.find(Filters.eq(Constant._id, missionId)).first();
        logsRepository.addLogs(LOG_TYPE,"删除",before,after);
    }

    public void updateMission(Mission mission, Long updatedBy){
        Bson filter = Filters.and(
                Filters.eq(Constant._id, mission.getMissionId()),
                Updates.set(Constant.isDeleted, false),
                Filters.eq(Constant.version, mission.getVersion()),
                Filters.ne("transform",2));
        Mission before = this.coll.find(Filters.eq(Constant._id, mission.getMissionId())).first();
        UpdateResult result = this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                Updates.set("materialCode", mission.getMaterialCode()),
                Updates.set("aoCode", mission.getAoCode()),
                Updates.set("count", mission.getCount()),
                Updates.set("date", mission.getDate()),
                Updates.set("time", mission.getTime()),
                Updates.set("dateTime", mission.getDateTime()),
                Updates.set("transform", 1),
                Updates.inc(Constant.version,1)
        ));
        if(result.getModifiedCount()==0){
            throw new IllegalArgumentException("找不到数据或者数据已被修改");
        }
        Mission after = this.coll.find(Filters.eq(Constant._id, mission.getMissionId())).first();
        logsRepository.addLogs(LOG_TYPE,"修改",before,after);
    }

    public PageResult<Mission> missionPageResult(ReqPageMission query, Pagination pagination) {
        Bson sort = Sorts.descending(Constant.createdOn);
        Bson filter = Filters.eq(Constant.isDeleted, Boolean.FALSE);
        if (query.getMaterialCode() != null) {
            filter = Filters.and(Filters.eq(Constant.materialCode, query.getMaterialCode()), filter);
        }
        if (query.getAoCode() != null) {
            filter = Filters.and(Filters.eq(Constant.aoCode, query.getAoCode()), filter);
        }
        return MongoPageQuery.builder(coll, Mission.class).sort(sort).page(pagination).filter(filter).execute();
    }

    public List<Mission> findUnTransformMission(){
        Bson sort = Sorts.ascending("dateTime");
        Bson filter = Filters.and(
                Filters.lt("dateTime", new Date()),
                Updates.set(Constant.isDeleted, false),
                Filters.eq("transform",1)
                );
        return this.coll.find(filter).sort(sort).into(new ArrayList<>());
    }

    public void transformSuccess(List<String> missionIds){
        Bson filter = Filters.and(Filters.in(Constant._id, missionIds), Filters.eq("transform",1));
        this.coll.updateMany(filter, AccessUtils.prepareUpdates(1L, "系统",
                Updates.set("transform", 2),
                Updates.inc(Constant.version,1)
        ));
    }
    public void transformError(List<String> missionIds){
        Bson filter = Filters.and(Filters.in(Constant._id, missionIds), Filters.eq("transform",1));
        this.coll.updateMany(filter, AccessUtils.prepareUpdates(1L, "系统",
                Updates.set("transform", 3),
                Updates.inc(Constant.version,1)
        ));
    }

}
