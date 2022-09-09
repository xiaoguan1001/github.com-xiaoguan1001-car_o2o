package cn.wolfcode.car.business.serivce.impl;

import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.domain.CarPackageAudit;
import cn.wolfcode.car.business.domain.Leave;
import cn.wolfcode.car.business.domain.ServiceItem;
import cn.wolfcode.car.business.mapper.BpmnInfoMapper;
import cn.wolfcode.car.business.mapper.CarPackageAuditMapper;
import cn.wolfcode.car.business.mapper.ServiceItemMapper;
import cn.wolfcode.car.business.query.CarPackageAuditQuery;
import cn.wolfcode.car.business.serivce.IBpmnInfoService;
import cn.wolfcode.car.business.serivce.ICarPackageAuditService;
import cn.wolfcode.car.business.serivce.ILeaveService;
import cn.wolfcode.car.business.serivce.IServiceItemService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import cn.wolfcode.car.shiro.ShiroUtils;
import com.github.pagehelper.PageHelper;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class CarPackageAuditServiceImpl implements ICarPackageAuditService {

    @Autowired
    private CarPackageAuditMapper carPackageAuditMapper;
    @Autowired
    private IBpmnInfoService bpmnInfoService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private IServiceItemService serviceItemService;

    @Autowired
    private ILeaveService leaveService;

    @Override
    public TablePageInfo<CarPackageAudit> query(CarPackageAuditQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());

        return new TablePageInfo<CarPackageAudit>(carPackageAuditMapper.selectForList(qo));
    }



    @Override
    public void save(CarPackageAudit carPackageAudit) {
        carPackageAuditMapper.insert(carPackageAudit);
    }

    @Override
    public CarPackageAudit get(Long id) {
        return carPackageAuditMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(CarPackageAudit carPackageAudit) {
        carPackageAuditMapper.updateByPrimaryKey(carPackageAudit);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            // 跟改显示的状态
            carPackageAuditMapper.deleteByPrimaryKey(dictId);
        }

    }

    @Override
    public List<CarPackageAudit> list() {
        return carPackageAuditMapper.selectAll();
    }

    @Override
    public InputStream processImg(Long id) {
        // 根据流程实列的id值查询对应的流程实列走到的地方
        CarPackageAudit carPackageAudit = this.get(id);
        BpmnInfo bpmnInfo = bpmnInfoService.get(carPackageAudit.getBpmnInfoId());
        ProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();
        BpmnModel model = repositoryService.getBpmnModel(bpmnInfo.getActProcessId());

        InputStream inputStream = null;


        // 查看流程状态是否在审核中
        if (CarPackageAudit.STATUS_IN_ROGRESS.equals(carPackageAudit.getStatus())) {
            Task task = taskService.createTaskQuery().processInstanceId(carPackageAudit.getInstanceId()).singleResult();
            List<String> activeActivityIds = runtimeService.getActiveActivityIds(task.getExecutionId());
            inputStream = generator.generateDiagram(model, activeActivityIds, Collections.EMPTY_LIST, "宋体","宋体","宋体");

        }else {
            //generateDiagram(流程模型,需要高亮的节点,需要高亮的线条,后面三个参数都表示是字体)
            inputStream = generator.generateDiagram(model, Collections.EMPTY_LIST, Collections.EMPTY_LIST, "宋体","宋体","宋体");
        }


        return inputStream;
    }

    // 审核撤销操作
    public void cancelApply(Long id) {

        CarPackageAudit carPackageAudit = this.get(id);
        // 修改项目单项的状态为初始化
        serviceItemService.updateAuditStatus(carPackageAudit.getServiceItemId(),ServiceItem.AUDITSTATUS_INIT);

        // 修改carPackageAudit中的状态信息
        this.updateAuditStatus(id,CarPackageAudit.STATUS_CANCEL);

        // 停止掉运行的任务信息 processInstanceId
        runtimeService.deleteParticipantGroup(carPackageAudit.getInstanceId(),"审核流程撤销");

    }

    public void updateAuditStatus(Long id, Integer status) {
        carPackageAuditMapper.updateAuditStatus(id,status);
    }

    // 流程的审核
    public void audit(Long id, int auditStatus, String info) {
        // 判断审核条件 需要是审核中的状态
        CarPackageAudit carPackageAudit = carPackageAuditMapper.selectByPrimaryKey(id);

        if (carPackageAudit == null || ! CarPackageAudit.STATUS_IN_ROGRESS.equals(carPackageAudit.getStatus())) {
            throw new BusinessException("参数异常");
        }

        Leave leave = leaveService.get(carPackageAudit.getServiceItemId());

        // 添加评论的信息
        String comment = "";
        // 获取当前评审人是谁
        String userName = ShiroUtils.getUser().getUserName();
        // 判断是否是审核通过状态
        if (CarPackageAudit.STATUS_PASS.equals(auditStatus)) {
            comment = carPackageAudit.getInfo() +"[]"+ userName+" 审核【通过】" + " , 并附加了评论 : " + info;
            carPackageAudit.setInfo(comment);

        }else {
            comment = carPackageAudit.getInfo() +"[]"+ userName+" 审核【拒绝】" + " , 并附加了评论 : " + info;
            carPackageAudit.setInfo(comment);
        }

        // 执行审核流程
        // 获取指定当前的执行流程
        Task currentlyTask = taskService.createTaskQuery().processInstanceId(carPackageAudit.getInstanceId()).singleResult();
        // 审核通过的操作 服务单项的状态 流程审核表的状态 运行的流程实列

        // 添加对应的流程条件
        taskService.setVariable(currentlyTask.getId(),"auditStatus",auditStatus);
        // 添加备注的信息内容
        taskService.addComment(currentlyTask.getId(),carPackageAudit.getInstanceId(),comment);

        // 启动流程
        taskService.complete(currentlyTask.getId());
        Task newTask = taskService.createTaskQuery().processInstanceId(carPackageAudit.getInstanceId()).singleResult();


        // 判断是否有通过流程
        if (CarPackageAudit.STATUS_PASS.equals(auditStatus)) {
            // 如果通过了判断有没有下个节点如果直接结束流程 如果有下个节点记录一下 下个节点的审核人
            if (newTask != null) {
                // 将下一个任务节点负责人名称设置进去 财务审核
                carPackageAudit.setAuditorId(Long.valueOf(newTask.getAssignee()));
            }else {
                // 如果没有下一个节点直接判断流程结束 跟改流程状态
                serviceItemService.updateAuditStatus(carPackageAudit.getServiceItemId(),ServiceItem.AUDITSTATUS_APPROVED);
                carPackageAudit.setStatus(CarPackageAudit.STATUS_PASS);
                if (leave != null) {
                    leave.setStatus(Leave.AUDITSTATUS_APPROVED.toString());
                }
            }
        }else {
            // 审核拒绝的操作 服务单项的状态 流程审核表的状态 运行的流程实列  如果审核没有通过将服务单项设置成初始化状态 对应的流程审核应为拒绝状态
            serviceItemService.updateAuditStatus(carPackageAudit.getServiceItemId(),ServiceItem.AUDITSTATUS_INIT);
            carPackageAudit.setStatus(CarPackageAudit.STATUS_REJECT);
            if (leave != null) {
                leave.setStatus(Leave.STATUS_REJECT.toString());
            }
        }
        // 结束审核的时间
        carPackageAudit.setAuditTime(new Date());

        if (leave != null) {
            leaveService.update(leave);
        }

        carPackageAuditMapper.updateByPrimaryKey(carPackageAudit);
    }


}
