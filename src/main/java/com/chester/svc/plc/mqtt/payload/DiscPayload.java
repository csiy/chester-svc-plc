package com.chester.svc.plc.mqtt.payload;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class DiscPayload extends Payload{
    private Long missionId;
    private List<SetDiscList> setDiscList = new ArrayList<>();

    public DiscPayload(Long missionId,SetDiscList setDiscList){
        super("setDisc");
        this.missionId = missionId;
        this.setDiscList.add(setDiscList);
    }

    @Data
    public static class SetDiscList{
        private Integer discNo;
        private Integer totalOneB;
        private Integer totalB;
    }
}
