package com.chester.svc.plc.mongodb.repository;

import com.chester.cloud.support.mongodb.AccessUtils;
import com.chester.data.mongo.MongoInt64IdGenerator;
import com.chester.data.mongo.MongoPageQuery;
import com.chester.svc.plc.core.model.JobStatus;
import com.chester.svc.plc.mongodb.config.Constant;
import com.chester.svc.plc.mongodb.model.Job;
import com.chester.svc.plc.mongodb.config.MongoCollections;
import com.chester.svc.plc.mongodb.model.Job;
import com.chester.svc.plc.mongodb.model.Logs;
import com.chester.svc.plc.mongodb.model.Machine;
import com.chester.svc.plc.web.model.req.ReqPageJob;
import com.chester.svc.sys.mongodb.repository.UserRepository;
import com.chester.util.coll.Lists;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import lombok.extern.slf4j.Slf4j;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class JobRepository {
    @Resource
    private MongoDatabase db;
    private MongoCollection<Job> coll;
    @Resource
    private UserRepository userRepository;
    @Resource
    private LogsRepository logsRepository;

    private static final Logs.LogsType LOG_TYPE = Logs.LogsType.JOB;

    @PostConstruct
    public void afterPropertiesSet() {
        this.coll = db.getCollection(MongoCollections.job, Job.class);
    }

    public void addJobs(List<Job> jobs){
        this.coll.insertMany(jobs);
    }

    public void deleteJob(String jobId,Integer version, Long updatedBy){
        Bson filter = Filters.and(Filters.eq(Constant._id, jobId), Filters.eq(Constant.version, version));
        Job before = this.coll.find(Filters.eq(Constant._id, jobId)).first();
        UpdateResult result = this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                Updates.set(Constant.isDeleted, true),
                Updates.inc(Constant.version,1)
        ));
        if(result.getModifiedCount()==0){
            throw new IllegalArgumentException("找不到数据或者数据已被修改");
        }
        Job after = this.coll.find(Filters.eq(Constant._id, jobId)).first();
        logsRepository.addLogs(LOG_TYPE,"删除",before,after);
    }

    /**
     * 未排程工单
     * @param pagination
     * @return
     */
    public PageResult<Job> jobUnSchedulerPageResult(List<Machine> list,Pagination pagination) {
        Bson sort = Sorts.ascending(Constant.createdOn);
        List<String> dishKeys = Lists.map(list, Machine::getDishKey);
        Bson filter = Filters.and(
                Filters.eq(Constant.isDeleted, Boolean.FALSE),
                Filters.eq(Constant.jobStatus, 0),
                Filters.not(Filters.in("dishKey", dishKeys))
                );
        return MongoPageQuery.builder(coll, Job.class).sort(sort).page(pagination).filter(filter).execute();
    }

    public Job getJob(String jobId){
        Bson filter = Filters.eq(Constant._id, jobId);
        return this.coll.find(filter).first();
    }

    /**
     * 获取已排程
     * @param machine
     * @return
     */
    public List<Job> getJobList(Machine machine){
        List<Job> result = new ArrayList<>();
        if(machine!=null&&machine.getDishKey()!=null&&machine.getLinkState()){
            String dishKey = machine.getDishKey();
            String materialCode = machine.getRuntimeMaterialCode();
            Bson sort = Sorts.ascending(Constant.createdOn);
            Bson filter = Filters.and(
                    Filters.eq(Constant.isDeleted, Boolean.FALSE),
                    Filters.eq(Constant.jobStatus, 0),
                    Filters.eq("machineId", ""),
                    Filters.eq("dishKey", dishKey)
            );
            List<Job> list = this.coll.find(filter).sort(sort).into(new ArrayList<>());
            if(!Lists.isEmpty(list)){
                Map<String,List<Job>> subJobs = Lists.groupBy(list,v->v.getMaterial().getMaterialCode());
                List<String> materials = Lists.map(list,v->v.getMaterial().getMaterialCode());
                materials = Lists.stream(materials).distinct().collect(Collectors.toList());
                if(materialCode!=null&&materials.contains(materialCode)){
                    result.addAll(subJobs.get(materialCode));
                    materials.remove(materialCode);
                }
                for(String key : materials){
                    result.addAll(subJobs.get(key));
                }
            }
        }
        return result;
    }

    public void setJobMachine(String jobId,String machineId){
        this.coll.updateOne(Filters.eq(Constant._id, jobId), AccessUtils.prepareUpdates(1L, "系统",
                Updates.set("machineId",machineId)
        ));
    }

    public Job getNextJob(Machine machine){
        List<Job> list = getJobList(machine);
        if(list.size()>0){
            return list.get(0);
        }
        return null;
    }

    public void updateMission(String jobId,Integer missionStatus){
        this.coll.updateOne(Filters.eq(Constant._id, jobId), AccessUtils.prepareUpdates(1L, "系统",
                Updates.set("jobStatus",missionStatus),
                Updates.set("isFinish",missionStatus==1)
        ));
    }
}
