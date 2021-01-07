package com.chester.svc.plc.mongodb.repository;

import com.chester.cloud.support.model.BaseEntity;
import com.chester.cloud.support.mongodb.AccessUtils;
import com.chester.svc.plc.mongodb.model.Logs;
import com.chester.svc.plc.mongodb.config.MongoCollections;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Repository
public class LogsRepository {
    @Resource
    private MongoDatabase db;
    private MongoCollection<Logs> coll;

    @PostConstruct
    public void afterPropertiesSet() {
        this.coll = db.getCollection(MongoCollections.logs, Logs.class);
    }

    public void addLogs(Logs.LogsType logsType,String remark, BaseEntity log){
        Logs logs = new Logs();
        logs.setLogsType(logsType);
        logs.setRemark(remark);
        logs.setAfter(log);
        logs.setBefore(log);
        this.coll.insertOne(logs);
    }
    public void addLogs(Logs.LogsType logsType,String remark, BaseEntity before, BaseEntity after){
        Logs logs = new Logs();
        logs.setLogsType(logsType);
        logs.setRemark(remark);
        logs.setAfter(after);
        logs.setBefore(before);
        this.coll.insertOne(logs);
    }
}
