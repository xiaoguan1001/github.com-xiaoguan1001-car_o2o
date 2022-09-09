package cn.wolfcode.car.business.serivce;


import cn.wolfcode.car.business.domain.Department;

import cn.wolfcode.car.common.base.page.TablePageInfo;

import java.util.List;

/**
 * 部门服务接口
 */
public interface IDepartmentService {




    /**
     * 查单个
     * @param id
     * @return
     */
    Department get(Long id);


    /**
     * 保存
     * @param department
     */
    void save(Department department);

  
    /**
     * 更新
     * @param department
     */
    void update(Department department);

    /**
     *  批量删除
     * @param ids
     */
    void deleteBatch(String ids);

    /**
     * 查询全部
     * @return
     */
    List<Department> list();
    
}
