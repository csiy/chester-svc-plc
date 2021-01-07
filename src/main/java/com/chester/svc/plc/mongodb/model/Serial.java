package com.chester.svc.plc.mongodb.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("流水号")
public class Serial {
    @ApiModelProperty("日期")
    private String date;
    @ApiModelProperty("流水号")
    private Long serialNumber;
    @ApiModelProperty("批次号")
    private Integer batchNumber;
}
