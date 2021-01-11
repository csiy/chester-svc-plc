package com.chester.svc.sys.web.model.req;

import lombok.Data;

import java.util.List;

@Data
public class ReqMenu {
    private Long role;
    private List<Long> menuIds;
}
