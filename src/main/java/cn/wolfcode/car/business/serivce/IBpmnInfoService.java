package cn.wolfcode.car.business.serivce;


import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.query.BpmnInfoQuery;
import cn.wolfcode.car.common.base.page.TablePageInfo;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

/**
 * 岗位服务接口
 */
public interface IBpmnInfoService {

    /**
     * 分页
     * @param qo
     * @return
     */
    TablePageInfo<BpmnInfo> query(BpmnInfoQuery qo);


    /**
     * 查单个
     * @param id
     * @return
     */
    BpmnInfo get(Long id);


    /**
     * 保存
     * @param bpmnInfo
     */
    void save(BpmnInfo bpmnInfo);

  
    /**
     * 更新
     * @param bpmnInfo
     */
    void update(BpmnInfo bpmnInfo);

    /**
     *  批量删除
     * @param ids
     */
    void deleteBatch(String ids);

    /**
     * 查询全部
     * @return
     */
    List<BpmnInfo> list();


    /**
     * 流程文件的部署 与添加对应的数据到数据库的表中
     * @param path
     * @param bpmnType
     * @param info
     */
    void deploy(String path, String bpmnType, String info) throws FileNotFoundException;

    /**
     * 根据前端传输过来的参数给页面做展示 仅限是 xml 或者是 png 格式
     * @param deploymentId
     * @param type
     * @return
     */
    InputStream readResource(Long deploymentId, String type);

    /**
     * 根据传递过来的id值删除掉所有的流程定义相关的内容
     * @param id
     */
    void delete(Long id);

    /**
     * 根据流程的类型查找流程审核
     * @param carPackage
     * @return
     */
    BpmnInfo queryByBpmnType(String carPackage);
}
