package com.chester.svc.sys.web.model.req;

import lombok.Data;

@Data
public class ReqPage {
    private Integer curPage = 1;
    private Integer pageSize = 10;
}
