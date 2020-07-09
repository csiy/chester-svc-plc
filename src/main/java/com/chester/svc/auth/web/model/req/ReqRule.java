package com.chester.svc.auth.web.model.req;

import lombok.Data;
import org.bson.types.ObjectId;

import java.util.List;

@Data
public class ReqRule {
    ObjectId ruleId;
    List<String> roles;
}
