package cn.wolfcode.car.business.serivce;


import cn.wolfcode.car.business.domain.CarPackageAudit;
import cn.wolfcode.car.business.query.CarPackageAuditQuery;
import cn.wolfcode.car.common.base.page.TablePageInfo;

import java.io.InputStream;
import java.util.List;

/**
 * 审核流程服务接口
 */
public interface ICarPackageAuditService {

    /**
     * 分页
     * @param qo
     * @return
     */
    TablePageInfo<CarPackageAudit> query(CarPackageAuditQuery qo);


    /**
     * 查单个
     * @param id
     * @return
     */
    CarPackageAudit get(Long id);


    /**
     * 保存
     * @param carPackageAudit
     */
    void save(CarPackageAudit carPackageAudit);

  
    /**
     * 更新
     * @param carPackageAudit
     */
    void update(CarPackageAudit carPackageAudit);

    /**
     *  批量删除
     * @param ids
     */
    void deleteBatch(String ids);

    /**
     * 查询全部
     * @return
     */
    List<CarPackageAudit> list();


    /**
     * 根据id展示流程进度
     * @param id
     * @return
     */
    InputStream processImg(Long id);

    /**
     * 审核撤销
     * @param id
     */
    void cancelApply(Long id);


    /**
     * 修改审核的状态值
     * @param id
     * @param status
     */
    void updateAuditStatus(Long id, Integer status);

    /**
     * 流程的审核
     * @param id
     * @param auditStatus
     * @param info
     */
    void audit(Long id, int auditStatus, String info);
}
