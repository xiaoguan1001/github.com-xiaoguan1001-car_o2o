package cn.wolfcode.car.business.serivce.impl;

import cn.wolfcode.car.business.domain.Employee;
import cn.wolfcode.car.business.mapper.EmployeeMapper;
import cn.wolfcode.car.business.query.EmployeeQuery;
import cn.wolfcode.car.business.serivce.IEmployeeService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class EmployeeServiceImpl implements IEmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;


    @Override
    public TablePageInfo<Employee> query(EmployeeQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());

        return new TablePageInfo<Employee>(employeeMapper.selectForList(qo));
    }



    @Override
    public void save(Employee employee) {
        employee.setHiredate(new Date());
        employee.setStatus(Employee.STATUS_ON);
        employeeMapper.insert(employee);
    }

    @Override
    public Employee get(Long id) {
        return employeeMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(Employee employee) {

        employeeMapper.updateByPrimaryKey(employee);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            // 跟改显示的状态
            employeeMapper.deleteByPrimaryKey(dictId);
        }

    }

    @Override
    public List<Employee> list() {
        return employeeMapper.selectAll();
    }



}
