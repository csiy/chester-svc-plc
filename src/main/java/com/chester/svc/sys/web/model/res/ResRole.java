package com.chester.svc.sys.web.model.res;

import lombok.Data;

@Data
public class ResRole {
    private Long roleId;
    private String name;
    private String remark;
    private Boolean modify;
}
