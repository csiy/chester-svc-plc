package com.chester.svc.auth.web.model.res;

import com.chester.data.mongo.annotations._id;
import lombok.Data;
import org.bson.types.ObjectId;

import java.util.List;

@Data
public class ResRule {
    @_id
    ObjectId ruleId;
    String path;
    List<String> roles;
    String type;
    String remark;
    String model;
    Boolean modify;
}
