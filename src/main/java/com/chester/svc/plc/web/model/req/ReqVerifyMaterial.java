package com.chester.svc.plc.web.model.req;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("物料校验")
public class ReqVerifyMaterial {
    List<ReqMaterial> materials;
}
