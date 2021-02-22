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
    @ApiModelProperty("包装数量")
    private Integer count;
    @ApiModelProperty("包装袋尺寸 高 mm")
    private Integer height;
    @ApiModelProperty("包装袋尺寸 宽 mm")
    private Integer width = 110;
    @ApiModelProperty("高速震动量")
    private Integer highSpeed;
    @ApiModelProperty("低速震动量")
    private Integer lowSpeed;
    @ApiModelProperty("检字号")
    private String verify;
}
