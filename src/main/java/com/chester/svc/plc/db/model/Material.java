package com.chester.svc.plc.db.model;

import com.chester.svc.sys.db.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel("包装数据模板")
@Entity
@Table(name = "plc_material")
public class Material extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("Id")
    private Long materialId;
    @ApiModelProperty("物料号")
    private String materialCode;
    @ApiModelProperty("ao工序号")
    private String aoCode;
    @ApiModelProperty("盘号")
    private String disk;
    @ApiModelProperty("定额数量")
    private Integer quantity;
    @ApiModelProperty("生产站位")
    private String position;
    @ApiModelProperty("存储BIN位")
    private String bin;
}
