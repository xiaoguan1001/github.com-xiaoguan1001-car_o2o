package cn.wolfcode.car.business.query;

import cn.wolfcode.car.common.base.query.QueryObject;
import lombok.Getter;
import lombok.Setter;
import org.activiti.engine.impl.bpmn.data.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Calendar;
import java.util.Date;

@Setter
@Getter
public class BpmnInfoQuery extends QueryObject {
    // 开始时间
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date beginTime;
    // 结束时间
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;

    // 将最后的试讲转换成当天的最后一秒
    public Date getEndTime(){
        if (endTime != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(endTime);
            calendar.set(Calendar.HOUR_OF_DAY,23);
            calendar.set(Calendar.MINUTE,59);
            calendar.set(Calendar.SECOND,59);
            // 返回设置的当天最后时间
            return calendar.getTime();
        }
        return null;
    }

}
