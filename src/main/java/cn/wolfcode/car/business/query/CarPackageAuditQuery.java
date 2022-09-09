package cn.wolfcode.car.business.query;

import cn.wolfcode.car.common.base.query.QueryObject;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Calendar;
import java.util.Date;

@Setter
@Getter
public class CarPackageAuditQuery extends QueryObject {

    // 登录用户的信息
    private Long auditorId;
    // 过滤筛选的状态
    private Integer status;

    // 已办查询根据备注信息查询已办内容
    private String info;


    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date beginTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endTime;


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
