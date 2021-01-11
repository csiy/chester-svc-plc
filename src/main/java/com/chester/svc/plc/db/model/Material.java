package com.chester.svc.plc.db.model;

import com.chester.svc.support.model.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("包装数据模板")
@Entity
@Table(name = "plc_material")
public class Material extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("Id")
    private Long materialId;
    @ApiModelProperty("key")
    private String key;
    @ApiModelProperty("物料名称")
    private String materialName;
    @ApiModelProperty("类型")
    private String type;
    @ApiModelProperty("物料号")
    private String materialCode;
    @ApiModelProperty("盘号")
    private String disk;
    @ApiModelProperty("定额数量")
    private Integer quantity;
    @ApiModelProperty("ao工序号")
    private String aoCode;
    @ApiModelProperty("生产站位")
    private String position;
    @ApiModelProperty("代换新号")
    private String replace;
    @ApiModelProperty("原定额代换")
    private String original;
    @ApiModelProperty("存储区域")
    private String store;
    @ApiModelProperty("存储BIN位")
    private String bin;
}
