package cn.wolfcode.car.business.serivce.impl;

import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.domain.CarPackageAudit;
import cn.wolfcode.car.business.domain.ServiceItem;
import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.mapper.BpmnInfoMapper;
import cn.wolfcode.car.business.mapper.CarPackageAuditMapper;
import cn.wolfcode.car.business.mapper.ServiceItemMapper;
import cn.wolfcode.car.business.mapper.StatementMapper;
import cn.wolfcode.car.business.query.ServiceItemQuery;
import cn.wolfcode.car.business.serivce.IBpmnInfoService;
import cn.wolfcode.car.business.serivce.IServiceItemService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import cn.wolfcode.car.shiro.ShiroUtils;
import com.alibaba.druid.sql.visitor.functions.If;
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
public class ServieceItemServiceImpl implements IServiceItemService {

    @Autowired
    private ServiceItemMapper serviceItemMapper;

    @Autowired
    private StatementMapper statementMapper;

    @Autowired
    private CarPackageAuditMapper carPackageAuditMapper;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private IBpmnInfoService bpmnInfoService;

    @Override
    public TablePageInfo<ServiceItem> query(ServiceItemQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<ServiceItem>(serviceItemMapper.selectForList(qo));
    }



    @Override
    public void save(ServiceItem serviceItem) {

        // 添加一个时间
        serviceItem.setCreateTime(new Date());

        // 判断是否是套餐 是套餐 设置初始化 不是套餐 无需审核
        if (ServiceItem.CARPACKAGE_YES.equals(serviceItem.getCarPackage())) {
            // 是套餐 设置初始化
            serviceItem.setCarPackage(ServiceItem.CARPACKAGE_YES);
            // 如果是套餐 审核默认是初始化状态
            serviceItem.setAuditStatus(ServiceItem.AUDITSTATUS_INIT);
            // 套餐默认是下架状态
            serviceItem.setSaleStatus(ServiceItem.SALESTATUS_OFF);

        }else {
            // 不是套餐 无需审核 直接上架
            serviceItem.setAuditStatus(ServiceItem.AUDITSTATUS_NO_REQUIRED);

            serviceItem.setCarPackage(ServiceItem.CARPACKAGE_NO);
            serviceItem.setSaleStatus(ServiceItem.SALESTATUS_ON);

        }

        serviceItemMapper.insert(serviceItem);
    }

    @Override
    public ServiceItem get(Long id) {
        return serviceItemMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(ServiceItem serviceItem) {
        // 根据id 把数据库中数据查询出来
        ServiceItem item = serviceItemMapper.selectByPrimaryKey(serviceItem.getId());
    // 判断他状态如果是上架状态或者是审核状态抛出异常

        if (ServiceItem.SALESTATUS_ON.equals(item.getSaleStatus())
                ||ServiceItem.AUDITSTATUS_AUDITING.equals(item.getAuditStatus())) {
        throw new BusinessException("非法操作");
    }
    // 如果是套餐并且是审核通过状态 只要编辑吧审核状态中修改成初始化状态
        if (ServiceItem.CARPACKAGE_YES.equals(item.getCarPackage())
                &&ServiceItem.AUDITSTATUS_APPROVED.equals(item.getAuditStatus())) {
        item.setAuditStatus(ServiceItem.AUDITSTATUS_INIT);
    }
    // 把数据库值有哪些是不需要更新的 要在xml文件中把对应字段给删除掉
        item.setName(serviceItem.getName());
        item.setOriginalPrice(serviceItem.getOriginalPrice());
        item.setDiscountPrice(serviceItem.getDiscountPrice());
        item.setServiceCatalog(serviceItem.getServiceCatalog());
        item.setInfo(serviceItem.getInfo());

        serviceItemMapper.updateByPrimaryKey(item);
}

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            serviceItemMapper.deleteByPrimaryKey(dictId);
        }
    }

    @Override
    public List<ServiceItem> list() {
        return serviceItemMapper.selectAll();
    }

    /**
     * 上架操作
     * @param id
     */
    @Override
    public void saleOff(Long id) {
        // 根据id把数据库中数据查询出来
        ServiceItem item = serviceItemMapper.selectByPrimaryKey(id);
        // 判断上架状态 如果是审核状态抛出异常
        if (ServiceItem.AUDITSTATUS_AUDITING.equals(item.getAuditStatus())) {
            throw new BusinessException("非法操作");
        }

        // 如果是套餐但是审核状态是非审核如果状态抛出异常
        if (ServiceItem.CARPACKAGE_YES.equals(item.getCarPackage())
                && !ServiceItem.AUDITSTATUS_APPROVED.equals(item.getAuditStatus())) {
            throw new BusinessException("非法操作");
        }
        // 修改上架状态
        serviceItemMapper.modifyStatus(ServiceItem.SALESTATUS_ON,id);
    }


    /**
     * 下架操作
     * @param id
     */
    @Override
    public void saleOn(Long id) {
        // 根据id把数据库中数据查询出来
        ServiceItem item = serviceItemMapper.selectByPrimaryKey(id);
        // 判断下架状态 如果为下架抛出异常
        if (ServiceItem.SALESTATUS_OFF.equals(item.getSaleStatus())) {
            throw new BusinessException("非法操作");
        }


        // 修改下架状态
        serviceItemMapper.modifyStatus(ServiceItem.SALESTATUS_OFF,id);
    }

    @Override
    public void updateAuditStatus(Long id, Integer status) {
        serviceItemMapper.updateAuditStatus(id,status);
    }

    @Override
    public void startAudit(Long id, Long bpmnInfoId, Long director, Long finance, String info) {
        // 判断是否满足条件
        ServiceItem serviceItem = this.get(id);

        if (serviceItem == null) {
            throw new BusinessException("没有该该参数");
        }
        if (! ServiceItem.AUDITSTATUS_INIT.equals(serviceItem.getAuditStatus())
                &&! ServiceItem.CARPACKAGE_YES.equals(serviceItem.getCarPackage())) {
            // 不是套餐与初始化状态都将抛出异常信息
            throw new BusinessException("参数错误,请重新查看");
        }
        // 套餐服务单项状态 审核中 AUDITSTATUS_AUDITING

        // 修改状态值
        serviceItemMapper.updateAuditStatus(serviceItem.getId(),ServiceItem.AUDITSTATUS_AUDITING);

        // 添加套餐审核记录 carPackageAudit
        // 封装 carPackageAudit 添加到数据库中
        CarPackageAudit carPackageAudit = new CarPackageAudit();
        //服务单项备注
        carPackageAudit.setInfo(info);
        //创建者
        carPackageAudit.setCreator(ShiroUtils.getUser().getUserName());
        //服务单项审核价格
        carPackageAudit.setServiceItemPrice(serviceItem.getDiscountPrice());
        //状态
        carPackageAudit.setStatus(CarPackageAudit.STATUS_IN_ROGRESS);
        //审计记录
        carPackageAudit.setServiceItemId(serviceItem.getId());
        //服务单项备注
        carPackageAudit.setServiceItemInfo(serviceItem.getInfo());
        //关联流程id
        carPackageAudit.setBpmnInfoId(bpmnInfoId);
        //当前审核人id
        carPackageAudit.setAuditorId(director);
        //创建时间
        carPackageAudit.setCreateTime(new Date());
        // 保存
        carPackageAuditMapper.insert(carPackageAudit);

        // 设置相关参数 启动流程
        // 获取bpmnINfo 对应的值
        BpmnInfo bpmnInfo = bpmnInfoService.get(bpmnInfoId);
        String processDefinitionKey = bpmnInfo.getActProcessKey();
        String businessKey = carPackageAudit.getId().toString();

        Map<String,Object> map = new HashMap<>();
        // 获得折扣价
        map.put("discountPrice", serviceItem.getDiscountPrice().longValue());

        if (director != null) {
            map.put("director", director);
        }

        if (finance != null) {
            map.put("finance", finance);
        }

        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey, businessKey, map);

        carPackageAuditMapper.updateInstanceId(carPackageAudit.getId(),processInstance.getId());
    }


}
