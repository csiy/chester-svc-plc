package com.chester.svc.plc.mongodb.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data()
@ApiModel("WM外部接口导入")
public class WM {
    @ApiModelProperty("波次号")
    private String waveNo;
    @ApiModelProperty("物料号")
    private String materialCode;
    @ApiModelProperty("ao工序号")
    private String aoCode;
    @ApiModelProperty("包装数量")
    private Integer count;
    @ApiModelProperty("包装袋尺寸")
    private Integer size;
    @ApiModelProperty("速率")
    private Integer speed;
}
