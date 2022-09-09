package cn.wolfcode.car.business.serivce.impl;

import cn.wolfcode.car.business.domain.Appointment;
import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.domain.StatementItem;
import cn.wolfcode.car.business.mapper.AppointmentMapper;
import cn.wolfcode.car.business.mapper.StatementItemMapper;
import cn.wolfcode.car.business.mapper.StatementMapper;
import cn.wolfcode.car.business.query.StatementQuery;
import cn.wolfcode.car.business.serivce.IStatementService;
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
public class StatementServiceImpl implements IStatementService {

    @Autowired
    private StatementMapper statementMapper;
    @Autowired
    private AppointmentMapper appointmentMapper;
    @Autowired
    private StatementItemMapper statementItemMapper;

    @Override
    public TablePageInfo<Statement> query(StatementQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<Statement>(statementMapper.selectForList(qo));
    }



    @Override
    public void save(Statement statement) {
        // 添加订单的创建时间
        statement.setCreateTime(new Date());
        // 添加状态 默认消费中的状态
        statement.setStatus(Statement.STATUS_CONSUME);
        statementMapper.insert(statement);
    }

    @Override
    public Statement get(Long id) {
        return statementMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(Statement statement) {
        // 修改操作先查看该用户是否处于消费状态 否则抛出异常
        Statement st = this.get(statement.getId());
        if (st != null && ! Statement.STATUS_CONSUME.equals(st.getStatus())) {
            throw new BusinessException("非法操作");
        }

        statementMapper.updateByStatus(statement);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            // 跟改显示的状态
            statementMapper.deleteByPrimaryKey(dictId);
        }

    }

    @Override
    public List<Statement> list() {
        return statementMapper.selectAll();
    }


    public Long itemDetail(Long id) {
        // 通过预约订单的id值查询出服务结算单 如果服务结算单中没有该详情 我们给服务结算单进行添加操作
        // 我们需要查询出服务订单的id 并判断是否为空 如果不为空则返回该条数据的id值
        Appointment appointment = appointmentMapper.selectByPrimaryKey(id);
        if (Appointment.STATUS_ARRIVAL.equals(appointment.getStatus()) || Appointment.STATUS_SETTLE.equals(appointment.getStatus())) {
            // 对应的预约订单值也需要进行修改
            appointmentMapper.updateStatus(appointment.getId(),Appointment.STATUS_SETTLE);
            Statement statement = null;
            // 如果服务结算单中有值的话直接返回该值
            statement = statementMapper.selectByAppointmentId(appointment.getId());
            if (statement == null) {
                statement = new Statement();
                //客户姓名
                statement.setCustomerName(appointment.getCustomerName());
                //客户联系方式
                statement.setCustomerPhone(appointment.getCustomerPhone());
                //实际到店时间
                statement.setActualArrivalTime(new Date());
                //车牌号码
                statement.setLicensePlate(appointment.getLicensePlate());
                //汽车类型
                statement.setCarSeries(appointment.getCarSeries());
                //服务类型
                statement.setServiceType(appointment.getServiceType().longValue());
                //创建时间
                statement.setCreateTime(appointment.getCreateTime());
                //备注信息
                statement.setInfo(appointment.getInfo());
                //结算状态 消费中
                statement.setStatus(Statement.STATUS_CONSUME);
                //预约单ID
                statement.setAppointmentId(appointment.getId());
                statementMapper.insert(statement);



                return statement.getId();
            }
            return statement.getId();
        }else {
            throw new BusinessException("只有到店或者结算才能进行查看结算单");
        }
    }


    public void deleteRelation(Long id) {
        Statement statement = statementMapper.selectByPrimaryKey(id);
        // 查询出服务是否是预约用户
        Appointment appointment = appointmentMapper.selectByPrimaryKey(statement.getAppointmentId());
        // 将如果是预约用户需要将预约用户的状态进行修改
        if (appointment != null) {
            // 将状态改成到店状态
            appointmentMapper.updateStatus(appointment.getId(),Appointment.STATUS_ARRIVAL);
        }

        // 删除服务结算细节 根据结算订单删除结算订单详情的内容
        statementItemMapper.deleteByStatementId(statement.getId());

        // 删除服务结算单
        statementMapper.deleteByPrimaryKey(statement.getId());

    }


}
