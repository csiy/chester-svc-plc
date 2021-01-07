package com.chester.svc.plc.mongodb.repository;

import com.chester.svc.plc.mongodb.config.MongoCollections;
import com.chester.svc.plc.mongodb.model.Material;
import com.chester.svc.plc.web.model.req.ReqPageMaterial;
import com.chester.svc.plc.mongodb.config.Constant;
import com.chester.svc.sys.db.repository.UserRepository;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Repository
public class MaterialRepository {
    @Resource
    private UserRepository userRepository;


    public void addMaterial(Material material, Long createdBy) {

    }

    public void deleteMaterial(String materialId,Integer version, Long updatedBy){

    }

    public void updateMaterial(Material material, Long updatedBy){

    }

    public Material getMaterial(String materialCode,String aoCode){
        return null;
    }

    public PageResult<Material> materialPageResult(ReqPageMaterial query, Pagination pagination) {
        return null;
    }

}
