package cn.wolfcode.car.business.mapper;

import cn.wolfcode.car.business.domain.Appointment;
import cn.wolfcode.car.business.query.AppointmentQuery;
import org.apache.ibatis.annotations.Param;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


public interface AppointmentMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Appointment record);

    Appointment selectByPrimaryKey(Long id);

    List<Appointment> selectAll();

    int updateByPrimaryKey(Appointment record);

    List<Appointment> selectForList(AppointmentQuery qo);

    void updateStatus(@Param("id") Long id, @Param("statusCancel") Integer statusCancel);

    void updateStatusArrival(@Param("id") Long id, @Param("statusArrival") Integer statusArrival, @Param("date") Date date);

    void updateShow(@Param("dictId") Long dictId, @Param("conceal_off") Integer conceal_off);

}