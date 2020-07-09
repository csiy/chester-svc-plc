package com.chester.svc.plc.mongodb.repository;

import com.chester.svc.plc.mongodb.model.Serial;
import com.chester.svc.plc.mongodb.config.MongoCollections;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.model.Updates;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDate;

@Repository
public class SerialRepository{
    @Resource
    private MongoDatabase db;
    private MongoCollection<Serial> coll;

    @PostConstruct
    public void afterPropertiesSet() {
        this.coll = db.getCollection(MongoCollections.serial, Serial.class);
    }

    public Long serialNumber() {
        LocalDate time = LocalDate.now();
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().upsert(true).returnDocument(ReturnDocument.AFTER);
        Serial serial = this.coll.findOneAndUpdate(
                Filters.eq("_id", time.toString()), 
                Updates.inc("serialNumber", 1L),
                options);
        return serial != null ? serial.getSerialNumber() : 0L;
    }

    public Integer batchNumber() {
        LocalDate time = LocalDate.now();
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().upsert(true).returnDocument(ReturnDocument.AFTER);
        Serial serial = this.coll.findOneAndUpdate(
                Filters.eq("_id", time.toString()),
                Updates.inc("batchNumber", 1),
                options);
        return serial != null ? serial.getBatchNumber() : 0;
    }
}
