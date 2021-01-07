package com.chester.svc.plc.web.model.req;

import com.chester.svc.support.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("物料存储")
public class ReqStorage extends BaseEntity {
    @ApiModelProperty("存储区域")
    private String area;
    @ApiModelProperty("BIN位")
    private String bin;
    @ApiModelProperty("物料Id")
    private String materialId;
    @ApiModelProperty("存储数量")
    private Integer count;
    @ApiModelProperty("版本号")
    private Integer version;
}
