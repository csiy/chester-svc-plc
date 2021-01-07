package com.chester.svc.sys.web.model.res;

import lombok.Data;

import java.util.List;

@Data
public class ResMenu {
    private String menuId;
    private String name;
    private String icon;
    private String url;
    private Integer sort;
    private String path;
    private String type;
    private Boolean modify;
    private List<String> parentIds;
    private List<String> roles;
}
