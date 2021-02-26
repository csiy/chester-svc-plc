package com.chester.svc.plc.mqtt.payload;

import com.chester.svc.plc.mongodb.config.Constant;
import com.chester.svc.plc.mongodb.model.Mission;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SwitchPayload extends Payload{
    @ApiModelProperty("任务编号")
    private String missionId;
    @ApiModelProperty("每包数量")
    private Integer totalOneB;
    @ApiModelProperty("总包数")
    private Integer totalB;
    @ApiModelProperty("开关")
    private String key;
    @ApiModelProperty("震动盘编号")
    private Integer discNo;
    @ApiModelProperty("包装袋尺寸 高 mm")
    private Integer height;
    @ApiModelProperty("包装袋尺寸 宽 mm")
    private Integer width = 110;
    @ApiModelProperty("高速速率")
    private Integer highSpeed;
    @ApiModelProperty("低速速率")
    private Integer lowSpeed;
    @ApiModelProperty("低速量")
    private Integer lowNumber;
    public SwitchPayload(String key,Integer discNo){
        super("switch");
        this.key = key;
        this.discNo = discNo;
    }

    public static SwitchPayload open(Mission next,Integer discNo){
        SwitchPayload payload = new SwitchPayload(Constant.open,discNo);
        payload.setTotalB(next.getCount());
        payload.setTotalOneB(next.getQuantity());
        payload.setMissionId(next.getMissionId());
        payload.setHeight(next.getHeight());
        payload.setWidth(next.getWidth());
        payload.setHighSpeed(next.getHighSpeed());
        payload.setLowSpeed(next.getLowSpeed());
        payload.setLowNumber(next.getLowNumber());
        return payload;
    }

    public static SwitchPayload close(String missionId,Integer discNo){
        SwitchPayload payload = new SwitchPayload(Constant.close,discNo);
        payload.setMissionId(missionId);
        return payload;
    }
}
