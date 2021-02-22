package com.chester.svc.plc.mqtt.payload;

import com.chester.svc.plc.mongodb.config.Constant;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SwitchPayload extends Payload{
    private String missionId;
    private Integer totalOneB;
    private Integer totalB;
    private String key;
    private Integer discNo;
    private Integer height;
    private Integer width;
    private Integer highSpeed;
    private Integer lowSpeed;
    public SwitchPayload(String key,Integer discNo){
        super("switch");
        this.key = key;
        this.discNo = discNo;
    }

    public static SwitchPayload open(String missionId,
                                     Integer totalOneB,
                                     Integer totalB,
                                     Integer discNo,
                                     Integer height,
                                     Integer width,
                                     Integer highSpeed,
                                     Integer lowSpeed){
        SwitchPayload payload = new SwitchPayload(Constant.open,discNo);
        payload.setTotalB(totalB);
        payload.setTotalOneB(totalOneB);
        payload.setMissionId(missionId);
        payload.setHeight(height);
        payload.setWidth(width);
        payload.setHeight(highSpeed);
        payload.setLowSpeed(lowSpeed);
        return payload;
    }

    public static SwitchPayload close(String missionId,Integer discNo){
        SwitchPayload payload = new SwitchPayload(Constant.open,discNo);
        payload.setMissionId(missionId);
        return payload;
    }
}
