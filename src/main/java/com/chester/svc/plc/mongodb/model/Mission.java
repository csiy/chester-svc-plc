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
    @ApiModelProperty("波次号")
    private String WAVENO;
    @ApiModelProperty("AO工序")
    private String SOREFERENCE3;
    @ApiModelProperty("件号")
    private String SKU;
    @ApiModelProperty("描述")
    private String descr_c;
    @ApiModelProperty("数量")
    private String qty_each;
    @ApiModelProperty("仓位")
    private String location;
    @ApiModelProperty("炉批号")
    private String lotatt05;
    @ApiModelProperty("检字号")
    private String lotatt08;
    @ApiModelProperty("发货日期")
    private String Shiptime;
    @ApiModelProperty("AO版本")
    private String Userdefine2;
    @ApiModelProperty("架次")
    private String soreference2;
    @ApiModelProperty("配套标签序号")
    private String d_edi_05;
    @ApiModelProperty("发运单号")
    private String ORDERNO;
    @ApiModelProperty("波次编组")
    private String Wavegroupno;
    @ApiModelProperty("分配ID")
    private String AllocationDetailsID;
    @ApiModelProperty("盘位")
    private String reservedfield04;
    @ApiModelProperty("包装袋尺寸")
    private String d_edi_06;
    @ApiModelProperty("速率")
    private String d_edi_07;
    @ApiModelProperty("操作人")
    private String editwho;

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
