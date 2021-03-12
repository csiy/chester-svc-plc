package com.chester.svc.plc.mongodb.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data()
@ApiModel("WM外部接口导入")
public class WM {
    @ApiModelProperty("分配ID")
    private String waveNo;
    @ApiModelProperty("物料号")
    private String materialCode;
    @ApiModelProperty("ao工序号")
    private String aoCode;
    @ApiModelProperty("包装数量 包数")
    private Integer count;
    @ApiModelProperty("定额数量 每包数量")
    private Integer quantity;
    @ApiModelProperty("包装袋尺寸 高 mm")
    private Integer height;
    @ApiModelProperty("包装袋尺寸 宽 mm")
    private Integer width = 110;
    @ApiModelProperty("高速速率")
    private Integer highSpeed;
    @ApiModelProperty("低速速率")
    private Integer lowSpeed;
    @ApiModelProperty("低速量")
    private Integer lowNumber;
    @ApiModelProperty("检字号")
    private String verify;
}
