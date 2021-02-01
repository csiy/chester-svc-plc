package com.chester.svc.plc.mongodb.model;

import com.chester.cloud.support.model.BaseEntity;
import com.chester.data.mongo.annotations._id;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel("包装任务")
public class Mission extends BaseEntity {
    @_id
    @ApiModelProperty("任务Id")
    private String missionId;
    @ApiModelProperty("流水号")
    private Long serialNumber;
    @ApiModelProperty("行项目号")
    private Integer lineNumber;
    @ApiModelProperty("导入日期")
    private String date;
    @ApiModelProperty("导入批次")
    private Integer batchNumber;
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
    @ApiModelProperty("包装数量")
    private Integer count;
    @ApiModelProperty("机器Id")
    private String machineId;
    @ApiModelProperty("排序")
    private Long sort;
    @ApiModelProperty("当前工单 任务状态 0未执行 1进行中 2任务完成 3任务异常终止")
    private Integer status;
    @ApiModelProperty("分配ID")
    private String waveNo;
    @ApiModelProperty("包装袋尺寸")
    private Integer size = 1;
    @ApiModelProperty("速率")
    private Integer speed = 1;
    @ApiModelProperty("检字号")
    private String verify;


    public void initMaterial(Material material){
        this.materialCode = material.getMaterialCode();
        this.disk = material.getDisk();
        this.quantity = material.getQuantity();
        this.aoCode = material.getAoCode();
        this.position = material.getPosition();
        this.replace = material.getReplace();
        this.original = material.getOriginal();
        this.store = material.getStore();
        this.bin = material.getBin();
    }
}
