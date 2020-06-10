package com.chester.svc.sys.mongodb.repository;

import com.chester.auth.client.Menu;
import com.chester.cloud.support.mongodb.AccessUtils;
import com.chester.svc.sys.mongodb.config.Constant;
import com.chester.svc.sys.mongodb.config.MongoCollections;
import com.chester.svc.sys.web.model.req.ReqMenu;
import com.chester.svc.sys.web.model.req.ReqMenuUpdate;
import com.chester.svc.sys.web.model.res.ResMenu;
import com.chester.util.coll.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Updates;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Repository

@Data
@Slf4j
public class MenuRepository {
    @Resource
    private MongoDatabase db;
    private MongoCollection<Menu> coll;

    @PostConstruct
    public void afterPropertiesSet() {
        this.coll = db.getCollection(MongoCollections.menu, Menu.class);
        this.coll.createIndex(Indexes.descending("url"), new IndexOptions().unique(true));
    }

    public void addMenu(Menu menu,Long createdBy){
        Menu find = this.coll.find(Filters.eq("url",menu.getUrl())).first();
        if(find!=null){
            return;
        }
        AccessUtils.prepareEntityBeforeInstall(menu, createdBy);
        if(menu.getParentIds()==null){
            menu.setParentIds(new ArrayList<>());
        }
        this.coll.insertOne(menu);
    }

    public void updateMenu(ReqMenuUpdate menu, Long updatedBy){
        if(!Lists.isEmpty(menu.getParentIds())){
            Bson parentFilter = Filters.and(Filters.in(Constant._id, menu.getParentIds()),Filters.eq(Constant.modify, true));
            this.coll.updateMany(parentFilter, AccessUtils.prepareUpdates(updatedBy,
                    Updates.addEachToSet(Constant.roles, menu.getRoles())
            ));
        }
        Bson filter = Filters.and(Filters.eq(Constant._id, menu.getMenuId()),Filters.eq(Constant.modify, true));
        this.coll.updateOne(filter, AccessUtils.prepareUpdates(updatedBy,
                Updates.set(Constant.roles, menu.getRoles())
        ));
    }

    public void pullMenu(ReqMenu menu, Long updatedBy){
        Bson filter = Filters.and(Filters.in(Constant._id, menu.getMenuIds()),Filters.eq(Constant.modify, true));
        this.coll.updateMany(filter, AccessUtils.prepareUpdates(updatedBy,
                Updates.pull(Constant.roles, menu.getRole())
        ));
    }

    public void pushMenu(ReqMenu menu,Long updatedBy){
        Bson filter = Filters.and(Filters.in(Constant._id, menu.getMenuIds()),Filters.eq(Constant.modify, true));
        this.coll.updateMany(filter, AccessUtils.prepareUpdates(updatedBy,
                Updates.addToSet(Constant.roles, menu.getRole())
        ));
    }

    public List<ResMenu> findMenu(){
        Bson filter = Filters.eq(Constant.modify, true);
        return this.coll.find(filter,ResMenu.class).into(new ArrayList<>());
    }

    public List<ResMenu> findMenu(List<String> roles){
        Bson filter = Filters.and(Filters.in(Constant.roles, roles),Filters.eq(Constant.isDeleted, false));
        return this.coll.find(filter,ResMenu.class).into(new ArrayList<>());
    }

}
