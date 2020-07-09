package com.chester.svc.plc.web.model.res;

import com.chester.cloud.support.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonProperty;

@Data
@ApiModel("物料")
public class ResMaterial extends BaseEntity {
    @BsonProperty("_id")
    @ApiModelProperty("物料Id")
    private String materialId;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("描述")
    private String description;
    @ApiModelProperty("类型")
    private String type;
    @ApiModelProperty("挡位")
    private Integer gears;
    @ApiModelProperty("版本号")
    private Integer version;
}
