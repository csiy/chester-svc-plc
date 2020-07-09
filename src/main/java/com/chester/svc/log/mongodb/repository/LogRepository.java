package com.chester.svc.log.mongodb.repository;

import com.chester.auth.client.aop.Logs;
import com.chester.svc.log.mongodb.config.MongoCollections;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Indexes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Slf4j
@Repository
public class LogRepository {
    @Resource
    private MongoDatabase db;

    private MongoCollection<Logs> coll;

    @PostConstruct
    public void afterPropertiesSet() {
        this.coll = db.getCollection(MongoCollections.log, Logs.class);
        this.coll.createIndex(Indexes.descending("startTime"));
    }

    public void addLog(Logs logs) {
        this.coll.insertOne(logs);
    }
}
