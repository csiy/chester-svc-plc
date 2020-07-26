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
    @ApiModelProperty("当前盘号")
    private Integer runtimeDishNumber;
    @ApiModelProperty("当前盘")
    private MachineDish runtimeDish;
    @ApiModelProperty("当前盘Key")
    private String dishKey;
    @ApiModelProperty("当前工单")
    private String runtimeJob;
    @ApiModelProperty("当前工单参数设置状态")
    private Integer runtimeJobSetStatus;
    @ApiModelProperty("当前物料号")
    private String runtimeMaterialId;
    @ApiModelProperty("当前工单 任务状态 0 当前无任务 1任务进行中 2 任务完成 3任务异常终止")
    private Integer runtimeJobStatus;
    @ApiModelProperty("机器链接状态")
    private Boolean linkState;
    @ApiModelProperty("机器运行状态")
    private Boolean runState;
    @ApiModelProperty("机器错误状态")
    private Integer errorCode;
    @ApiModelProperty("链接超时时间")
    private Long lostThreshold;

    @Data
    @ApiModel("机器盘")
    public static class MachineDish{
        @ApiModelProperty("盘号")
        private DishEnum dish;
        @ApiModelProperty("挡位")
        private GearsEnum gears;
    }
}
