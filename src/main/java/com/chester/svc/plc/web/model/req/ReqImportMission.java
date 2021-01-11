package com.chester.svc.plc.web.model.req;

import com.chester.svc.plc.db.model.Mission;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("导入任务")
public class ReqImportMission {
    @ApiModelProperty("任务列表")
    private List<Mission> missions;
}
