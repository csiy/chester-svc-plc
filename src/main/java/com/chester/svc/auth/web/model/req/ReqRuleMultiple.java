package com.chester.svc.auth.web.model.req;

import lombok.Data;

import java.util.List;

@Data
public class ReqRuleMultiple {
    List<Integer> ruleIds;
    String role;
}
