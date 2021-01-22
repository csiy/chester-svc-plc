package com.chester.svc.plc.mongodb.model;

import com.chester.data.mongo.annotations._id;
import lombok.Data;

@Data
public class WMS {
    @_id
    private String WAVENO;//波次号
    private String SOREFERENCE3;//AO工序
    private String SKU;//件号
    private String descr_c;//描述
    private String qty_each;//数量
    private String location;//仓位
    private String lotatt05;//炉批号
    private String lotatt08;//检字号
    private String Shiptime;//发货日期
    private String Userdefine2;//AO版本
    private String soreference2;//架次
    private String d_edi_05;//配套标签序号
    private String ORDERNO;//发运单号
    private String Wavegroupno;//波次编组
    private String AllocationDetailsID;//分配ID
    private String reservedfield04;//盘位
    private String d_edi_06;//包装袋尺寸
    private String d_edi_07;//速率
    private String editwho;//操作人
    private String missionId;
}
