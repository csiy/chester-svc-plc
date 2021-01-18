package com.chester.svc.plc.mongodb.model;

import com.chester.cloud.support.model.BaseEntity;
import com.chester.data.mongo.annotations._id;
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
    private List<Disk> disks;
    @ApiModelProperty("机器链接状态")
    private Boolean linkState;
    @ApiModelProperty("链接超时时间")
    private Long lostThreshold;
    @Data
    @ApiModel("盘号")
    public static class Disk{
        @ApiModelProperty("盘号")
        private String name;
        @ApiModelProperty("任务ID")
        private String missionId;
        @ApiModelProperty("排程数量")
        private Integer scheduler;
    }
}
