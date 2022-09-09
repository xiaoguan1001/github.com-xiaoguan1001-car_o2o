package cn.wolfcode.car.business.mapper;

import cn.wolfcode.car.business.domain.CarPackageAudit;
import cn.wolfcode.car.business.query.CarPackageAuditQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CarPackageAuditMapper {
    int deleteByPrimaryKey(Long id);

    int insert(CarPackageAudit record);

    CarPackageAudit selectByPrimaryKey(Long id);

    List<CarPackageAudit> selectAll();

    int updateByPrimaryKey(CarPackageAudit record);

    List<CarPackageAudit> selectForList(CarPackageAuditQuery qo);


    /**
     * 修改流程实列的id
     * @param id
     * @param processdefinitionId
     */
    void updateInstanceId(@Param("id") Long id, @Param("processdefinitionId") String processdefinitionId);

    /**
     * 修改审核流程状态
     * @param id
     * @param status
     */
    void updateAuditStatus(@Param("id") Long id, @Param("status") Integer status);
}