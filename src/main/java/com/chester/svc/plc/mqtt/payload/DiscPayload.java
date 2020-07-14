package com.chester.svc.plc.mqtt.payload;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DiscPayload extends Payload{
    private List<SetDiscList> setDiscList = new ArrayList<>();

    public DiscPayload(SetDiscList setDiscList){
        super("setDisc");
        this.setDiscList.add(setDiscList);
    }

    @Data
    public static class SetDiscList{
        private Integer discNo;
        private Integer totalOneB;
        private Integer totalB;
    }
}
