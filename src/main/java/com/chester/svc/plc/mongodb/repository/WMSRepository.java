package com.chester.svc.plc.mongodb.repository;

import com.chester.svc.plc.mongodb.config.Constant;
import com.chester.svc.plc.mongodb.config.MongoCollections;
import com.chester.svc.plc.mongodb.model.WMS;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Slf4j
@Repository
public class WMSRepository {
    @Resource
    private MongoDatabase db;
    private MongoCollection<WMS> coll;
    @PostConstruct
    public void afterPropertiesSet() {
        this.coll = db.getCollection(MongoCollections.machine, WMS.class);
    }
    public void add(WMS wms){
        this.coll.insertOne(wms);
    }

    public WMS get(String WAVENO){
        return this.coll.find(Filters.eq(Constant._id,WAVENO)).first();
    }
}
