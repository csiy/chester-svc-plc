package com.chester.svc.plc.mongodb.repository;

import com.chester.cloud.support.mongodb.AccessUtils;
import com.chester.data.mongo.MongoInt64IdGenerator;
import com.chester.data.mongo.MongoPageQuery;
import com.chester.svc.http.MyHttpClientUtils;
import com.chester.svc.plc.mongodb.config.Constant;
import com.chester.svc.plc.mongodb.config.MongoCollections;
import com.chester.svc.plc.mongodb.model.Mission;
import com.chester.svc.plc.web.model.req.ReqPageMission;
import com.chester.svc.sys.mongodb.repository.UserRepository;
import com.chester.util.json.JSON;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
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
    private MongoInt64IdGenerator sortGenerator;
    @Resource
    private MyHttpClientUtils myHttpClientUtils;

    @PostConstruct
    public void afterPropertiesSet() {
        this.coll = db.getCollection(MongoCollections.mission, Mission.class);
        this.coll.createIndex(Indexes.ascending(Constant.date));
        this.coll.createIndex(Indexes.ascending(Constant.machineId, Constant.sort));
        this.coll.updateMany(Filters.eq(Constant.isDeleted, false), AccessUtils.prepareUpdates(1L, "系统",
                Updates.set("highSpeed", 140),
                Updates.set("lowSpeed", 125)
        ));

    }

    public void addMission(Mission mission, Long createdBy) {
        beforeAddMission(mission, createdBy);
        mission.setLineNumber(1);
        mission.setBatchNumber(serialRepository.batchNumber());
        mission.setDate(getDate(new Date()));
        this.coll.insertOne(mission);
    }

    public void importMission(List<Mission> missions, Long createdBy) {
        String inputTime = getDate(new Date());
        Integer batchNumber = serialRepository.batchNumber();
        for (int i = 0; i < missions.size(); i++) {
            Mission mission = missions.get(i);
            beforeAddMission(mission, createdBy);
            mission.setDate(inputTime);
            mission.setLineNumber(i + 1);
            mission.setBatchNumber(batchNumber);
        }
        this.coll.insertMany(missions);
    }

    public Mission getMission(String missionId) {
        return this.coll.find(Filters.eq(Constant._id, missionId)).first();
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
        if (query.getStatus() != null) {
            filter = Filters.and(Filters.eq(Constant.status, query.getStatus()), filter);
        }
        if (query.getDate() != null) {
            filter = Filters.and(Filters.eq(Constant.date, query.getDate()), filter);
        }
        return MongoPageQuery.builder(coll, Mission.class).sort(sort).page(pagination).filter(filter).execute();
    }

    public PageResult<Mission> findMissionByMachineIdAndDisk(String machineId, String disk, Pagination pagination) {
        Bson sort = Sorts.ascending(Constant.sort);
        Bson filter = Filters.and(
                Filters.eq(Constant.machineId, machineId),
                Filters.eq(Constant.disk, disk),
                Filters.eq(Constant.isDeleted, Boolean.FALSE),
                Filters.eq(Constant.status, 0)
        );
        return MongoPageQuery.builder(coll, Mission.class).sort(sort).page(pagination).filter(filter).execute();
    }

    public PageResult<Mission> unScheduler(Pagination pagination) {
        Bson sort = Sorts.ascending(Constant.sort);
        Bson filter = Filters.and(
                Filters.eq(Constant.machineId, Constant.empty),
                Filters.eq(Constant.isDeleted, Boolean.FALSE)
        );
        return MongoPageQuery.builder(coll, Mission.class).sort(sort).page(pagination).filter(filter).execute();
    }

    public void deleteMission(String missionId, Long updatedBy) {
        Bson filter = Filters.eq(Constant._id, missionId);
        this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                Updates.set(Constant.isDeleted, true)
        ));
    }

    public void updateMission(Mission mission, Long updatedBy) {
        Bson filter = Filters.and(
                Filters.eq(Constant._id, mission.getMissionId()),
                Filters.eq(Constant.isDeleted, Boolean.FALSE));
        this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                Updates.set(Constant.materialCode, mission.getMaterialCode()),
                Updates.set(Constant.aoCode, mission.getAoCode()),
                Updates.set(Constant.count, mission.getCount()),
                Updates.set(Constant.disk, mission.getDisk()),
                Updates.set(Constant.quantity, mission.getQuantity()),
                Updates.set(Constant.position, mission.getPosition()),
                Updates.set(Constant.replace, mission.getReplace()),
                Updates.set(Constant.original, mission.getOriginal()),
                Updates.set(Constant.store, mission.getStore()),
                Updates.set(Constant.bin, mission.getBin()),
                Updates.set(Constant.machineId, Constant.empty),
                Updates.set(Constant.status, 0)
        ));
    }

    public void updateDisk(Mission mission, Long updatedBy) {
        Bson filter = Filters.and(
                Filters.eq(Constant._id, mission.getMissionId()),
                Filters.eq(Constant.isDeleted, Boolean.FALSE));
        this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                Updates.set(Constant.disk, mission.getDisk())
        ));
    }

    public void updateMission(List<String> missionIds, String machineId) {
        this.coll.updateOne(Filters.in(Constant._id, missionIds), AccessUtils.prepareUpdates(1L, "系统",
                Updates.set(Constant.machineId, machineId)
        ));
    }

    public void updateMission(String machineId) {
        Bson filter = Filters.and(
                Filters.eq(Constant.status, 0),
                Filters.eq(Constant.machineId, machineId),
                Filters.eq(Constant.isDeleted, Boolean.FALSE));
        this.coll.updateMany(filter, AccessUtils.prepareUpdates(1L, "系统",
                Updates.set(Constant.machineId, Constant.empty)
        ));
    }

    public void updateMission(String missionId, Integer status) {
        this.coll.updateOne(Filters.eq(Constant._id, missionId), AccessUtils.prepareUpdates(1L, "系统",
                Updates.set(Constant.status, status)
        ));
    }

    public Mission getNext(String machineId, String disk) {
        Bson sort = Sorts.ascending(Constant.sort);
        Bson filter = Filters.and(
                Filters.eq(Constant.status, 0),
                Filters.eq(Constant.machineId, machineId),
                Filters.eq(Constant.isDeleted, Boolean.FALSE),
                Filters.eq(Constant.disk, disk));
        return this.coll.find(filter).sort(sort).first();
    }

    public void sort(String mission1, String mission2) {
        Mission _mission1 = getMission(mission1);
        Mission _mission2 = getMission(mission2);
        this.coll.updateOne(Filters.eq(Constant._id, mission1), Updates.set(Constant.sort, _mission2.getSort()));
        this.coll.updateOne(Filters.eq(Constant._id, mission2), Updates.set(Constant.sort, _mission1.getSort()));
    }

    public List<Mission> findAllUnSchedule() {
        Bson filter = Filters.and(
                Filters.eq(Constant.status, 0),
                Filters.eq(Constant.machineId, Constant.empty),
                Filters.eq(Constant.isDeleted, Boolean.FALSE));
        return this.coll.find(filter).into(new ArrayList<>());
    }

    public Mission getMissionByWave(String waveNo) {
        return this.coll.find(Filters.eq(Constant.waveNo, waveNo)
        ).first();
    }

    public static final String url = "http://183.234.148.212:19192/datahub/FluxWmsJsonApi/";

    public void print(String... ids) {
        if (ids != null && ids.length > 0) {
            List<NameValuePair> params = new ArrayList<>();
            Map<String, Object> data = new HashMap<>();
            List<Map<String, String>> header = new ArrayList<>();
            for (String id : ids) {
                if (id != null) {
                    Map<String, String> idMap = new HashMap<>();
                    idMap.put("ALLOCATIONDETAILSID", id);
                    header.add(idMap);
                }
            }
            if (header.size() > 0) {
                Map<String, Object> _header = new HashMap<>();
                _header.put("header", header);
                data.put("xmldata", _header);
                params.add(new BasicNameValuePair("method", "putPRINTData"));
                params.add(new BasicNameValuePair("client_customerid", "FLUXWMSASSJSON"));
                params.add(new BasicNameValuePair("client_db", "FLUXWMSDB"));
                params.add(new BasicNameValuePair("messageid", "PRINT"));
                params.add(new BasicNameValuePair("apptoken", "80AC1A3F-F949-492C-A024-7044B28C8025"));
                params.add(new BasicNameValuePair("appkey", "FLUX"));
                params.add(new BasicNameValuePair("sign", "NGRMMZG2MGFIOGJIOGIZZJLMZWU5MWE5NGU5MJGZNTI="));
                params.add(new BasicNameValuePair("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm:ss"))));
                params.add(new BasicNameValuePair("data", JSON.stringify(data)));
                log.info("print url :{} , params:{}", url, JSON.stringify(params));
                myHttpClientUtils.postWithParamsForString(url, params);
            }
        }
    }

    private String getDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    private void beforeAddMission(Mission mission, Long createdBy) {
        AccessUtils.prepareEntityBeforeInstall(mission, createdBy, userRepository.getUserName(createdBy));
        mission.setSerialNumber(serialRepository.serialNumber());
        mission.setMissionId(getMissionId(mission));
        mission.setMachineId(Constant.empty);
        mission.setSort(sortGenerator.generate());
        mission.setStatus(0);
    }

    private String getMissionId(Mission mission) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(mission.getCreatedOn()) +
                String.format("%06d", mission.getSerialNumber());
    }
}
