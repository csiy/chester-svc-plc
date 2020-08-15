package com.chester.svc.plc.mongodb.model;

import com.chester.cloud.support.model.BaseEntity;
import com.chester.data.mongo.annotations._id;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("包装数据模板")
public class Material extends BaseEntity {
    @_id
    @ApiModelProperty("Id")
    private String materialId;
    @ApiModelProperty("物料名称")
    private String materialName;
    @ApiModelProperty("类型")
    private String type;
    @ApiModelProperty("物料号")
    private String materialCode;
    @ApiModelProperty("盘号")
    private String disk;
    @ApiModelProperty("定额数量")
    private Integer quantity;
    @ApiModelProperty("ao工序号")
    private String aoCode;
    @ApiModelProperty("生产站位")
    private String position;
    @ApiModelProperty("代换新号")
    private String replace;
    @ApiModelProperty("原定额代换")
    private String original;
    @ApiModelProperty("存储区域")
    private String store;
    @ApiModelProperty("存储BIN位")
    private String bin;
    @ApiModelProperty("版本号")
    private Integer version;
}
