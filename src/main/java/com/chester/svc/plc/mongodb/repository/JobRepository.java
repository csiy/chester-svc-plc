package com.chester.svc.plc.mongodb.repository;

import com.chester.data.mongo.MongoInt64IdGenerator;
import com.chester.svc.plc.mongodb.model.Job;
import com.chester.svc.plc.mongodb.config.MongoCollections;
import com.chester.svc.sys.mongodb.repository.UserRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Repository
public class JobRepository {
    @Resource
    private MongoDatabase db;
    private MongoCollection<Job> coll;
    @Resource
    private UserRepository userRepository;

    @PostConstruct
    public void afterPropertiesSet() {
        this.coll = db.getCollection(MongoCollections.job, Job.class);
    }
}
