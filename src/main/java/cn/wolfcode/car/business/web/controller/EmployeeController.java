package cn.wolfcode.car.business.web.controller;


import cn.wolfcode.car.business.domain.Department;
import cn.wolfcode.car.business.domain.Employee;
import cn.wolfcode.car.business.query.EmployeeQuery;
import cn.wolfcode.car.business.serivce.IDepartmentService;
import cn.wolfcode.car.business.serivce.IEmployeeService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.web.AjaxResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 岗位控制器
 */
@Controller
@RequestMapping("business/employee")
public class EmployeeController {
    //模板前缀
    private static final String prefix = "business/employee/";

    @Autowired
    private IDepartmentService departmentService;

    @Autowired
    private IEmployeeService employeeService;

    //页面------------------------------------------------------------
    //列表
    @RequiresPermissions("employee:view")
    @RequestMapping("/listPage")
    public String listPage(){
        return prefix + "list";
    }

    @RequestMapping("/queryDept")
    public String queryDept(Long id,Model model){
        Department department = departmentService.get(id);
        model.addAttribute("department",department);

        return prefix + "queryDept";
    }





    @RequiresPermissions("employee:add")
    @RequestMapping("/addPage")
    public String addPage(){
        return prefix + "add";
    }


    @RequiresPermissions("employee:edit")
    @RequestMapping("/editPage")
    public String editPage(Long id, Model model){
        Employee employee = employeeService.get(id);
        model.addAttribute("employee",employee);
        return prefix + "edit";
    }

    //数据-----------------------------------------------------------
    //列表
    @RequiresPermissions("employee:list")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<Employee> query(EmployeeQuery qo){
        return employeeService.query(qo);
    }



    //新增
    @RequiresPermissions("employee:add")
    @RequestMapping("/add")
    @ResponseBody
    public AjaxResult addSave(Employee employee){
        employeeService.save(employee);
        return AjaxResult.success();
    }




    //编辑
    @RequiresPermissions("employee:edit")
    @RequestMapping("/edit")
    @ResponseBody
    public AjaxResult edit(Employee employee){
        employeeService.update(employee);
        return AjaxResult.success();
    }




    //删除
    @RequiresPermissions("employee:remove")
    @RequestMapping("/delete")
    @ResponseBody
    public AjaxResult delete(String id){
        employeeService.deleteBatch(id);
        return AjaxResult.success();
    }





}
