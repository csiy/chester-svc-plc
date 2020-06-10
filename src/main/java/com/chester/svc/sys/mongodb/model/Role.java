package com.chester.svc.sys.mongodb.model;

import com.chester.cloud.support.model.BaseEntity;
import com.chester.data.mongo.annotations._id;
import lombok.Data;

@Data
public class Role extends BaseEntity {
    @_id
    private Long roleId;
    private String name;
    private String remark;
    private Boolean modify = true;
}
