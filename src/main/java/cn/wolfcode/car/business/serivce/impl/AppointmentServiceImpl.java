package cn.wolfcode.car.business.serivce.impl;

import cn.wolfcode.car.business.domain.Appointment;
import cn.wolfcode.car.business.mapper.AppointmentMapper;
import cn.wolfcode.car.business.query.AppointmentQuery;
import cn.wolfcode.car.business.serivce.IAppointmentService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class AppointmentServiceImpl implements IAppointmentService {

    @Autowired
    private AppointmentMapper appointmentMapper;


    @Override
    public TablePageInfo<Appointment> query(AppointmentQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());

        return new TablePageInfo<Appointment>(appointmentMapper.selectForList(qo));
    }



    @Override
    public void save(Appointment appointment) {
        // 创建时间
        appointment.setCreateTime(new Date());
        // 状态为预约中
        appointment.setStatus(Appointment.STATUS_APPOINTMENT);

        appointmentMapper.insert(appointment);
    }

    @Override
    public Appointment get(Long id) {
        return appointmentMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(Appointment appointment) {

        // 查看出该修改的对象 并设置值更改修改的对象
        Appointment tment = this.get(appointment.getId());
        if (!Appointment.STATUS_APPOINTMENT.equals(tment.getStatus())) {
            // 如果不是预约中的状态抛出异常
            throw new BusinessException("非法操作");
        }
        tment.setCustomerName(appointment.getCustomerName());
        tment.setCustomerPhone(appointment.getCustomerPhone());
        tment.setAppointmentTime(appointment.getAppointmentTime());
        tment.setLicensePlate(appointment.getLicensePlate());
        tment.setCarSeries(appointment.getCarSeries());
        tment.setServiceType(appointment.getServiceType());
        tment.setInfo(appointment.getInfo());
        appointmentMapper.updateByPrimaryKey(tment);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            // 跟改显示的状态
            appointmentMapper.updateShow(dictId,Appointment.CONCEAL_OFF);
        }

    }

    @Override
    public List<Appointment> list() {
        return appointmentMapper.selectAll();
    }

    @Override
    public void cancelHandler(Long id) {
        // 查看该数据的状态 如果不是预约的状态抛出异常
        Appointment tment = this.get(id);
        if (!Appointment.STATUS_APPOINTMENT.equals(tment.getStatus())) {
            // 如果不是预约中的状态抛出异常
            throw new BusinessException("非法操作");
        }

        appointmentMapper.updateStatus(id,Appointment.STATUS_CANCEL);
    }

    @Override
    public void arrival(Long id) {

        // 查看该数据的状态 如果不是预约的状态抛出异常
        Appointment tment = this.get(id);
        if (!Appointment.STATUS_APPOINTMENT.equals(tment.getStatus())) {
            // 如果不是预约中的状态抛出异常
            throw new BusinessException("非法操作");
        }

        appointmentMapper.updateStatusArrival(id,Appointment.STATUS_ARRIVAL,new Date());
    }


}
