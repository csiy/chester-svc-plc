package com.chester.svc.plc.db.repository;

import com.chester.svc.plc.db.model.Mission;
import com.chester.svc.plc.web.model.req.ReqPageMission;
import com.chester.svc.sys.db.repository.UserRepository;
import com.chester.util.page.PageResult;
import com.chester.util.page.Pagination;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Repository
public class MissionRepository {
    @Resource
    private UserRepository userRepository;

    public void addMission(Mission mission, Long createdBy) {
    }

    public void importMission(List<Mission> missions, Long createdBy) {
        String inputTime = getDate(new Date());
        for (int i = 0; i < missions.size(); i++) {
            Mission mission = missions.get(i);
            beforeAddMission(mission, createdBy);
            mission.setInputTime(inputTime);
            mission.setLineNumber(i + 1);
        }
    }

    private String getDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    private Mission beforeAddMission(Mission mission, Long createdBy) {
        mission.setVersion(1);
        mission.setIsFinish(false);
        mission.setJobStatus(0);
        mission.setTransform(1);
        if (mission.getDateTime() == null) {
            mission.setDateTime(new Date());
        }
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        mission.setDate(getDate(mission.getDateTime()));
        mission.setTime(timeFormat.format(mission.getDateTime()));
        return mission;
    }

    private String getMissionId(Mission mission) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        StringBuilder builder = new StringBuilder();
        builder.append(sdf.format(mission.getCreatedOn()));
        builder.append(mission.getSerialNumber());
        return builder.toString();
    }

    public void deleteMission(String missionId, Integer version, Long updatedBy) {

    }

    public void updateMission(Mission mission, Long updatedBy) {
    }

    public PageResult<Mission> missionPageResult(ReqPageMission query, Pagination pagination) {
        return null;
    }

    public List<Mission> findUnTransformMission() {
        return null;
    }

    public void transformSuccess(List<Long> missionIds) {

    }

    public void transformError(List<Long> missionIds) {

    }

    public void updateMission(Long missionId, Integer missionStatus) {

    }

}
