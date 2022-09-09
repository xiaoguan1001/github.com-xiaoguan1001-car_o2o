package cn.wolfcode.car.business.serivce.impl;

import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.domain.CarPackageAudit;
import cn.wolfcode.car.business.domain.Leave;
import cn.wolfcode.car.business.domain.ServiceItem;
import cn.wolfcode.car.business.mapper.CarPackageAuditMapper;
import cn.wolfcode.car.business.mapper.LeaveMapper;
import cn.wolfcode.car.business.query.LeaveQuery;
import cn.wolfcode.car.business.serivce.IBpmnInfoService;
import cn.wolfcode.car.business.serivce.ICarPackageAuditService;
import cn.wolfcode.car.business.serivce.ILeaveService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import cn.wolfcode.car.shiro.ShiroUtils;
import com.github.pagehelper.PageHelper;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class LeaveServiceImpl implements ILeaveService {

    @Autowired
    private LeaveMapper leaveMapper;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private IBpmnInfoService bpmnInfoService;

    @Autowired
    private CarPackageAuditMapper carPackageAuditMapper;

    @Override
    public TablePageInfo<Leave> query(LeaveQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());

        return new TablePageInfo<Leave>(leaveMapper.selectForList(qo));
    }




    public void save(Leave leave) {
        leaveMapper.insert(leave);
    }

    @Override
    public Leave get(Long id) {
        return leaveMapper.selectOne(id);
    }

    @Override
    public void update(Leave leave) {

        leaveMapper.updateByPrimaryKey(leave);
    }



    @Override
    public List<Leave> list() {
        return leaveMapper.selectAll();
    }


    @Override
    public void startAudit(Long id,Long bpmnInfoId,Long director,Long finance,String info) {
        // 判断是否满足条件
        Leave leave = this.get(id);

        if (leave == null) {
            throw new BusinessException("没有该该参数");
        }

        if (! Leave.AUDITSTATUS_INIT.equals(Integer.valueOf(leave.getStatus()))) {
            throw new BusinessException("参数错误,请重新查看");
        }

        // 修改状态值

        // 添加套餐审核记录 carPackageAudit
        // 封装 carPackageAudit 添加到数据库中
        CarPackageAudit carPackageAudit = new CarPackageAudit();
        //服务单项备注
        carPackageAudit.setInfo(info);
        //创建者
        carPackageAudit.setCreator(ShiroUtils.getUser().getUserName());

        //状态
        carPackageAudit.setStatus(CarPackageAudit.STATUS_IN_ROGRESS);
        //审计记录
        carPackageAudit.setServiceItemId(leave.getId());
        //服务单项备注
        carPackageAudit.setServiceItemInfo(leave.getInfo());
        //关联流程id
        carPackageAudit.setBpmnInfoId(bpmnInfoId);
        //当前审核人id
        carPackageAudit.setAuditorId(director);
        //创建时间
        carPackageAudit.setCreateTime(new Date());
        // 保存
        carPackageAuditMapper.insert(carPackageAudit);


        // 修改状态值
        leave.setStatus(Leave.AUDITSTATUS_AUDITING.toString());
        leave.setAuditid(director.intValue());
        // 设置相关参数 启动流程
        // 获取bpmnINfo 对应的值
        BpmnInfo bpmnInfo = bpmnInfoService.get(bpmnInfoId);
        String processDefinitionKey = bpmnInfo.getActProcessKey();
        Map<String,Object> map = new HashMap<>();

        map.put("shopOwner", leave.getAuditid().longValue());
        String businessKey = leave.getId().toString();


        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey,map);


        leaveMapper.updateByPrimaryKey(leave);
        carPackageAuditMapper.updateInstanceId(carPackageAudit.getId(),processInstance.getId());
    }


}
