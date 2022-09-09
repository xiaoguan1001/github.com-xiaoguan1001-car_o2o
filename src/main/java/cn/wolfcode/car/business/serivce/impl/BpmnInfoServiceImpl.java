package cn.wolfcode.car.business.serivce.impl;

import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.mapper.BpmnInfoMapper;
import cn.wolfcode.car.business.mapper.StatementMapper;
import cn.wolfcode.car.business.query.BpmnInfoQuery;
import cn.wolfcode.car.business.serivce.IBpmnInfoService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.config.SystemConfig;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import com.github.pagehelper.PageHelper;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import oshi.util.platform.mac.SysctlUtil;

import java.io.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class BpmnInfoServiceImpl implements IBpmnInfoService {

    @Autowired
    private BpmnInfoMapper bpmnInfoMapper;

    // 流程引擎对象
    @Autowired
    protected RepositoryService repositoryService;


    @Override
    public TablePageInfo<BpmnInfo> query(BpmnInfoQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());
        return new TablePageInfo<BpmnInfo>(bpmnInfoMapper.selectForList(qo));
    }



    @Override
    public void save(BpmnInfo bpmnInfo) {
        bpmnInfoMapper.insert(bpmnInfo);
    }

    @Override
    public BpmnInfo get(Long id) {
        return bpmnInfoMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(BpmnInfo bpmnInfo) {

        bpmnInfoMapper.updateByPrimaryKey(bpmnInfo);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            bpmnInfoMapper.deleteByPrimaryKey(dictId);
        }
    }

    @Override
    public List<BpmnInfo> list() {
        return bpmnInfoMapper.selectAll();
    }



    public void deploy(String path, String bpmnType, String info) throws FileNotFoundException {
        // 启动流程引擎
        Deployment deployment = null;

        deployment = repositoryService.createDeployment().addInputStream(path,new FileInputStream(new File(SystemConfig.getProfile(),path))).deploy();
        if (deployment == null) {
            throw new BusinessException("没有找到流程图");
        }

        // 获取流程引擎对象的id 等
        BpmnInfo bpmnInfo = new BpmnInfo();
        //流程部署id
        bpmnInfo.setDeploymentId(deployment.getId());
        //流程(图)类型
        bpmnInfo.setBpmnType(bpmnType);
        // 流程图的部署路径
        bpmnInfo.setBpmnPath(path);
        //部署时间
        bpmnInfo.setDeployTime(deployment.getDeploymentTime());
        //描述信息
        bpmnInfo.setInfo(info);

        // latestVersion() 如果支持多个流程图进行部署只需要通过该方法获取多个
        // 通过部署的id去取到 Process 表中的字段信息 ( bpmn图属性表 )
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();

        //流程(图)名称
        bpmnInfo.setBpmnName(processDefinition.getName());
        //activity流程定义生成的主键
        bpmnInfo.setActProcessId(processDefinition.getId());
        //activity流程定义生成的key
        bpmnInfo.setActProcessKey(processDefinition.getKey());
        // 添加到数据库中
        bpmnInfoMapper.insert(bpmnInfo);
    }


    //前端图片页面的展示
    public InputStream readResource(Long deploymentId, String type) {
        InputStream inputStream = null;

        // 通过前端传输过来的 deploymentId 获取流程表中的 数据
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deploymentId.toString()).singleResult();

        // 根据前端传输过来的格式展示二进制文件到浏览器中
        if ("xml".equalsIgnoreCase(type)) {
            // 通过 deploymentId 与部署的资源名称将内容返回到前端的页面中进行展示;
            inputStream = repositoryService.getResourceAsStream(deploymentId.toString(), processDefinition.getResourceName());
        }else if ("png".equalsIgnoreCase(type)){
            BpmnModel model = repositoryService.getBpmnModel(processDefinition.getId());
            ProcessDiagramGenerator generator = new DefaultProcessDiagramGenerator();
            //generateDiagram(流程模型,需要高亮的节点,需要高亮的线条,后面三个参数都表示是字体)
            inputStream = generator.generateDiagram(model, Collections.EMPTY_LIST, Collections.EMPTY_LIST, "宋体","宋体","宋体");
        }
        return inputStream;
    }


    public void delete(Long id) {
        // 通过id  查询出对应的流程定义数据
        BpmnInfo bpmnInfo = this.get(id);
        // 拿到了该条数据将其删除掉
        bpmnInfoMapper.deleteByPrimaryKey(bpmnInfo.getId());

        // 删除掉文件所在文位置的文件
        File file = new File(SystemConfig.getProfile(), bpmnInfo.getBpmnPath());
        if (file.exists()) {
            // 判断该文件是否存在如果存在则将该文件删除掉
            file.delete();
        }
        // 通过启动的service删除掉所有的关于该id值的内容进行联级删除
        // (  删除给定的部署和级联删除到流程实例、历史流程实例和作业 )
        repositoryService.deleteDeployment(bpmnInfo.getDeploymentId(),true);
    }

    @Override
    public BpmnInfo queryByBpmnType(String carPackage) {

        return bpmnInfoMapper.queryByBpmnType(carPackage);
    }

}
