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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public void updateJob(Job job, Long updatedBy){
        Bson filter = Filters.and(
                Filters.eq(Constant._id, job.getJobId()),
                Filters.eq(Constant.version, job.getVersion()),
                Filters.eq(Constant.isDeleted, Boolean.FALSE),
                Filters.eq("jobStatus", JobStatus.CREATE)
                );
        Job before = this.coll.find(Filters.eq(Constant._id, job.getJobId())).first();
        UpdateResult result = this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                Updates.set("mission", job.getMission()),
                Updates.set("material", job.getMaterial()),
                Updates.set("machineId", ""),
                Updates.set("isError",false),
                Updates.inc(Constant.version,1)
        ));
        if(result.getModifiedCount()==0){
            throw new IllegalArgumentException("找不到数据或者数据已被修改");
        }
        Job after = this.coll.find(Filters.eq(Constant._id, job.getJobId())).first();
        logsRepository.addLogs(LOG_TYPE,"修改",before,after);
    }

    /**
     * 未排程工单
     * @param pagination
     * @return
     */
    public PageResult<Job> jobUnSchedulerPageResult(Pagination pagination) {
        Bson sort = Sorts.ascending(Constant.createdOn);
        Bson filter = Filters.and(Filters.eq(Constant.isDeleted, Boolean.FALSE),Filters.eq("machineId", ""));
        return MongoPageQuery.builder(coll, Job.class).sort(sort).page(pagination).filter(filter).execute();
    }

    /**
     * 查询所以未排程工单
     * @return
     */
    public List<Job> jobUnScheduler(){
        Bson sort = Sorts.ascending(Constant.createdOn);
        Bson filter = Filters.and(
                Filters.eq(Constant.isDeleted, Boolean.FALSE),
                Filters.eq("machineId", "")
        );
        return this.coll.find(filter).sort(sort).into(new ArrayList<>());
    }

    public Job getJob(String jobId){
        Bson filter = Filters.eq(Constant._id, jobId);
        return this.coll.find(filter).first();
    }

    public List<Job> getJobList(List<String> jobIds){
        Bson filter = Filters.in(Constant._id, jobIds);
        return this.coll.find(filter).into(new ArrayList<>());
    }

    /**
     * 释放排期
     */
    public void releaseScheduler(List<String> jobIds){
        log.info("重置排程列表 ：{}",jobIds);
        if(!Lists.isEmpty(jobIds)){
            Bson filter = Filters.in(Constant._id, jobIds);
            this.coll.updateMany(filter, AccessUtils.prepareUpdates(1L, "系统",
                    Updates.set("machineId", ""),
                    Updates.set("jobStatus", JobStatus.CREATE.toString()),
                    Updates.inc(Constant.version,1)
            ));
        }
    }

    /**
     * 排程
     */
    public void scheduler(List<String> jobIds,String machineId){
        Bson filter = Filters.in(Constant._id, jobIds);
        this.coll.updateMany(filter, AccessUtils.prepareUpdates(1L, "系统",
                Updates.set("machineId", machineId),
                Updates.set("isError",false),
                Updates.set("errorMessages", new ArrayList<>()),
                Updates.set("jobStatus", JobStatus.SCHEDULER.toString()),
                Updates.inc(Constant.version,1)
        ));
    }

    public void schedulerError(List<String> jobIds){
        Bson filter = Filters.in(Constant._id, jobIds);
        this.coll.updateMany(filter, AccessUtils.prepareUpdates(1L, "系统",
                Updates.set("isError",true),
                Updates.set("errorMessages", Arrays.asList("未找到对应盘")),
                Updates.inc(Constant.version,1)
        ));
    }
}
