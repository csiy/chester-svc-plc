package com.chester.svc.plc.mongodb.repository;

import com.chester.cloud.support.mongodb.AccessUtils;
import com.chester.data.mongo.MongoPageQuery;
import com.chester.svc.plc.mongodb.config.MongoCollections;
import com.chester.svc.plc.mongodb.model.Logs;
import com.chester.svc.plc.mongodb.model.Material;
import com.chester.svc.plc.web.model.req.ReqPageMaterial;
import com.chester.svc.plc.mongodb.config.Constant;
import com.chester.svc.sys.mongodb.repository.UserRepository;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import com.mongodb.client.result.UpdateResult;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Repository
public class MaterialRepository {
    @Resource
    private MongoDatabase db;
    private MongoCollection<Material> coll;
    @Resource
    private UserRepository userRepository;
    @Resource
    private LogsRepository logsRepository;
    private static final Logs.LogsType LOG_TYPE = Logs.LogsType.MATERIAL;


    @PostConstruct
    public void afterPropertiesSet() {
        this.coll = db.getCollection(MongoCollections.material, Material.class);
        this.coll.createIndex(Indexes.descending("materialCode", "aoCode"), new IndexOptions().unique(true));
    }

    public void addMaterial(Material material, Long createdBy) {
        AccessUtils.prepareEntityBeforeInstall(material, createdBy, userRepository.getUserName(createdBy));
        material.setMaterialId(ObjectId.get().toHexString());
        material.setVersion(1);
        try{
            this.coll.insertOne(material);
            logsRepository.addLogs(LOG_TYPE, "创建", material);
        }catch (MongoWriteException e){
            if(e.getError().getCode()==11000){
                Material material1 = getMaterial(material.getMaterialCode(),material.getAoCode());
                material.setMaterialId(material1.getMaterialId());
                material.setVersion(material1.getVersion());
                updateMaterial(material,createdBy);
            }
        }
    }

    public void deleteMaterial(String materialId,Integer version, Long updatedBy){
        Bson filter = Filters.and(Filters.eq(Constant._id, materialId), Filters.eq(Constant.version, version));
        Material before = this.coll.find(Filters.eq(Constant._id, materialId)).first();
        UpdateResult result = this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                Updates.set(Constant.isDeleted, true),
                Updates.inc(Constant.version,1)
        ));
        if(result.getModifiedCount()==0){
            throw new IllegalArgumentException("找不到数据或者数据已被修改");
        }
        Material after = this.coll.find(Filters.eq(Constant._id, materialId)).first();
        logsRepository.addLogs(LOG_TYPE,"删除",before,after);
    }

    public void updateMaterial(Material material, Long updatedBy){
        Bson filter = Filters.and(
                Filters.eq(Constant._id, material.getMaterialId()),
                Filters.eq(Constant.version, material.getVersion()),
                Filters.eq(Constant.isDeleted, Boolean.FALSE)
        );
        try{
            Material before = this.coll.find(Filters.eq(Constant._id, material.getMaterialId())).first();
            UpdateResult result = this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                    Updates.set("materialCode", material.getMaterialCode()),
                    Updates.set("gears", material.getGears().toString()),
                    Updates.set("dish", material.getDish().toString()),
                    Updates.set("quantity", material.getQuantity()),
                    Updates.set("aoCode", material.getAoCode()),
                    Updates.set("position", material.getPosition()),
                    Updates.set("replace", material.getReplace()),
                    Updates.set("original", material.getOriginal()),
                    Updates.set("store", material.getStore()),
                    Updates.set("bin", material.getBin()),
                    Updates.inc(Constant.version, 1)
            ));
            if(result.getModifiedCount()==0){
                throw new IllegalArgumentException("找不到数据或者数据已被修改");
            }
            Material after = this.coll.find(Filters.eq(Constant._id, material.getMaterialId())).first();
            logsRepository.addLogs(LOG_TYPE,"修改",before,after);
        }catch (MongoWriteException e){
            if(e.getError().getCode()==11000){
                throw new IllegalArgumentException("不能存在相同的物料号与AO工序号");
            }
            throw e;
        }
    }

    public Material getMaterial(String materialCode,String aoCode){
        return this.coll.find(
                Filters.and(Filters.eq("materialCode", materialCode),Filters.eq("aoCode", aoCode))
        ).first();
    }

    public PageResult<Material> materialPageResult(ReqPageMaterial query, Pagination pagination) {
        Bson sort = Sorts.descending(Constant.createdOn);
        Bson filter = Filters.eq(Constant.isDeleted, Boolean.FALSE);
        if (query.getMaterialCode() != null) {
            filter = Filters.and(Filters.eq(Constant.materialCode, query.getMaterialCode()), filter);
        }
        if (query.getAoCode() != null) {
            filter = Filters.and(Filters.eq(Constant.aoCode, query.getAoCode()), filter);
        }
        return MongoPageQuery.builder(coll, Material.class).sort(sort).page(pagination).filter(filter).execute();
    }

}
