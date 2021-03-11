package com.chester.svc.plc.mongodb.repository;

import com.chester.cloud.support.mongodb.AccessUtils;
import com.chester.data.mongo.MongoPageQuery;
import com.chester.svc.plc.mongodb.config.MongoCollections;
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

    @PostConstruct
    public void afterPropertiesSet() {
        this.coll = db.getCollection(MongoCollections.material, Material.class);
        this.coll.createIndex(Indexes.descending(Constant.materialCode, Constant.aoCode), new IndexOptions().unique(true));
    }

    public void addMaterial(Material material, Long createdBy) {
        AccessUtils.prepareEntityBeforeInstall(material, createdBy, userRepository.getUserName(createdBy));
        material.setMaterialId(ObjectId.get().toHexString());
        try {
            this.coll.insertOne(material);
        } catch (MongoWriteException e) {
            if (e.getError().getCode() == 11000) {
                Material material1 = getMaterial(material.getMaterialCode(), material.getAoCode());
                material.setMaterialId(material1.getMaterialId());
                updateMaterial(material, createdBy);
            }
        }
    }

    public void deleteMaterial(String materialId, Long updatedBy) {
        Bson filter = Filters.eq(Constant._id, materialId);
        this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                Updates.set(Constant.isDeleted, true)
        ));
    }

    public void updateMaterial(Material material, Long updatedBy) {
        Bson filter = Filters.eq(Constant._id, material.getMaterialId());
        this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy, userRepository.getUserName(updatedBy),
                Updates.set(Constant.materialCode, material.getMaterialCode()),
                Updates.set(Constant.disk, material.getDisk()),
                Updates.set(Constant.quantity, material.getQuantity()),
                Updates.set(Constant.aoCode, material.getAoCode()),
                Updates.set(Constant.position, material.getPosition()),
                Updates.set(Constant.replace, material.getReplace()),
                Updates.set(Constant.original, material.getOriginal()),
                Updates.set(Constant.store, material.getStore()),
                Updates.set(Constant.bin, material.getBin()),
                Updates.set(Constant.isDeleted, Boolean.FALSE)
        ));
    }

    public Material getMaterial(String materialCode, String aoCode) {
        return this.coll.find(
                Filters.and(
                        Filters.eq(Constant.materialCode, materialCode),
                        Filters.eq(Constant.aoCode, aoCode)
                )
        ).first();
    }

    public PageResult<Material> materialPageResult(ReqPageMaterial query, Pagination pagination) {
        Bson filter = Filters.eq(Constant.isDeleted, Boolean.FALSE);
        if (query.getMaterialCode() != null) {
            filter = Filters.and(Filters.eq(Constant.materialCode, query.getMaterialCode()), filter);
        }
        if (query.getAoCode() != null) {
            filter = Filters.and(Filters.eq(Constant.aoCode, query.getAoCode()), filter);
        }
        return MongoPageQuery.builder(coll, Material.class).sort(Sorts.descending(Constant.createdOn)).page(pagination).filter(filter).execute();
    }

}
