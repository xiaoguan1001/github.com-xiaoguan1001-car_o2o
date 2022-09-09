package cn.wolfcode.car.business.serivce;


import cn.wolfcode.car.business.domain.Leave;
import cn.wolfcode.car.business.query.LeaveQuery;
import cn.wolfcode.car.common.base.page.TablePageInfo;

import java.util.List;

/**
 * 岗位服务接口
 */
public interface ILeaveService {

    /**
     * 分页
     * @param qo
     * @return
     */
    TablePageInfo<Leave> query(LeaveQuery qo);


    /**
     * 查单个
     * @param id
     * @return
     */
    Leave get(Long id);


    /**
     * 保存
     * @param leave
     */
    void save(Leave leave);

  
    /**
     * 更新
     * @param leave
     */
    void update(Leave leave);



    /**
     * 查询全部
     * @return
     */
    List<Leave> list();

    /**
     * 请求流程流程实列的创建
     * @param id
     * @param bpmnInfoId
     * @param director
     * @param finance
     * @param info
     */
    void startAudit(Long id, Long bpmnInfoId, Long director, Long finance, String info);
}
