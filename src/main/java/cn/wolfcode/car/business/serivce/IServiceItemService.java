package cn.wolfcode.car.business.serivce;


import cn.wolfcode.car.business.domain.ServiceItem;
import cn.wolfcode.car.business.query.ServiceItemQuery;
import cn.wolfcode.car.common.base.page.TablePageInfo;

import java.util.List;

/**
 * 岗位服务接口
 */
public interface IServiceItemService {

    /**
     * 分页
     * @param qo
     * @return
     */
    TablePageInfo<ServiceItem> query(ServiceItemQuery qo);


    /**
     * 查单个
     * @param id
     * @return
     */
    ServiceItem get(Long id);


    /**
     * 保存
     * @param serviceItem
     */
    void save(ServiceItem serviceItem);

  
    /**
     * 更新
     * @param serviceItem
     */
    void update(ServiceItem serviceItem);

    /**
     *  批量删除
     * @param ids
     */
    void deleteBatch(String ids);

    /**
     * 查询全部
     * @return
     */
    List<ServiceItem> list();

    /**
     * 上架
     * @param id
     */
    void saleOff(Long id);

    /**
     * 下架
     * @param id
     */
    void saleOn(Long id);

    /**
     * 修改审核的状态值
     * @param id
     * @param status
     */
    void updateAuditStatus(Long id, Integer status);

    /**
     * 审核流程实列
     * @param id        服务项的id
     * @param bpmnInfoId bpmn 信息 ID
     * @param director  审核人id
     * @param finance  审核金额
     * @param info      备注信息
     */
    void startAudit(Long id, Long bpmnInfoId, Long director, Long finance, String info);
}
