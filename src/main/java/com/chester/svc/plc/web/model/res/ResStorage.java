package com.chester.svc.plc.web.model.res;

import com.chester.svc.support.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("货架")
public class ResStorage extends BaseEntity {
    @ApiModelProperty("存储Id")
    private String storageId;
    @ApiModelProperty("存储区域")
    private String area;
    @ApiModelProperty("BIN位")
    private String bin;
    @ApiModelProperty("物料Id")
    private ResMaterial material;
    @ApiModelProperty("存储数量")
    private Integer count;
    @ApiModelProperty("版本号")
    private Integer version;
}
