package com.chester.svc.sys.web.model.req;

import lombok.Data;

import java.util.List;

@Data
public class ReqMenuUpdate {
    private List<String> parentIds;
    private List<String> roles;
    private String menuId;
}
