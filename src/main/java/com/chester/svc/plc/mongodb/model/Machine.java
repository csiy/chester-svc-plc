package com.chester.svc.plc.mongodb.model;

import com.chester.cloud.support.model.BaseEntity;
import com.chester.data.mongo.annotations._id;
import com.chester.svc.plc.core.model.DishEnum;
import com.chester.svc.plc.core.model.GearsEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.swing.text.Document;
import java.util.List;

@Data
@ApiModel("机器")
public class Machine extends BaseEntity {
    @_id
    @ApiModelProperty("机器Id")
    private String machineId;
    @ApiModelProperty("位置")
    private String address;
    @ApiModelProperty("机器盘")
    private List<MachineDish> machineDishList;
    @ApiModelProperty("机器单片机参数")
    private Document params;
    @ApiModelProperty("版本号")
    private Integer version;

    @Data
    @ApiModel("机器盘")
    public static class MachineDish{
        @ApiModelProperty("盘号")
        private DishEnum dish;
        @ApiModelProperty("挡位")
        private GearsEnum gears;
    }
}
