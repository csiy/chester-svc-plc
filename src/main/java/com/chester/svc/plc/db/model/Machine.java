package com.chester.svc.plc.db.model;

import com.chester.svc.sys.db.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel("机器")
@Entity
@Table(name = "plc_machine")
public class Machine extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("机器Id")
    private Long machineId;
    @ApiModelProperty("key")
    private String key;
    @ApiModelProperty("位置")
    private String address;
    @ElementCollection
    @ApiModelProperty("机器盘")
    private List<String> disks;
    @ApiModelProperty("机器链接状态")
    private Boolean linkState;
    @ApiModelProperty("链接超时时间")
    private Long lostThreshold;
}
