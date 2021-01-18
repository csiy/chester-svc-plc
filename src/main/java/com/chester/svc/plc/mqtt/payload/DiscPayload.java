package com.chester.svc.plc.mqtt.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class DiscPayload extends Payload{
    private String missionId;
    private List<SetDiscList> setDiscList = new ArrayList<>();

    public DiscPayload(String missionId,SetDiscList setDiscList){
        super("setDisc");
        this.missionId = missionId;
        this.setDiscList.add(setDiscList);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SetDiscList{
        private Integer discNo;
        private Integer totalOneB;
        private Integer totalB;
    }
}
