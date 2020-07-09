package com.chester.svc.plc.mongodb.model;

import com.chester.data.mongo.annotations._id;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("流水号")
public class Serial {
    @_id
    @ApiModelProperty("日期")
    private String date;
    @ApiModelProperty("流水号")
    private Long serialNumber;
    @ApiModelProperty("批次号")
    private Integer batchNumber;
}
