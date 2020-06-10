package com.chester.svc.sys.web.model.res;

import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Data
public class ResRole {
    @BsonProperty("_id")
    private Long roleId;
    private String name;
    private String remark;
    private Boolean modify;
}
