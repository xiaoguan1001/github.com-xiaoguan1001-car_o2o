package cn.wolfcode.car.business.web.controller;


import cn.wolfcode.car.business.domain.Appointment;
import cn.wolfcode.car.business.query.AppointmentQuery;
import cn.wolfcode.car.business.serivce.IAppointmentService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.web.AjaxResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 岗位控制器
 */
@Controller
@RequestMapping("system/appointment")
public class AppointmentController {
    //模板前缀
    private static final String prefix = "business/appointment/";

    @Autowired
    private IAppointmentService appointmentService;

    //页面------------------------------------------------------------
    //列表
    @RequiresPermissions("appointment:view")
    @RequestMapping("/listPage")
    public String listPage(){
        return prefix + "list";
    }

    @RequiresPermissions("appointment:add")
    @RequestMapping("/addPage")
    public String addPage(){
        return prefix + "add";
    }


    @RequiresPermissions("appointment:edit")
    @RequestMapping("/editPage")
    public String editPage(Long id, Model model){
        model.addAttribute("appointment", appointmentService.get(id));
        return prefix + "edit";
    }

    //数据-----------------------------------------------------------
    //列表
    @RequiresPermissions("appointment:list")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<Appointment> query(AppointmentQuery qo){
        return appointmentService.query(qo);
    }



    //新增
    @RequiresPermissions("appointment:add")
    @RequestMapping("/add")
    @ResponseBody
    public AjaxResult addSave(Appointment appointment){
        appointmentService.save(appointment);
        return AjaxResult.success();
    }

    //取消操作
    @RequiresPermissions("appointment:cancelHandler")
    @RequestMapping("/cancelHandler")
    @ResponseBody
    public AjaxResult cancelHandler(Long id){
        appointmentService.cancelHandler(id);
        return AjaxResult.success();
    }
    //到店操作
    @RequiresPermissions("appointment:arrival")
    @RequestMapping("/arrival")
    @ResponseBody
    public AjaxResult arrival(Long id){
        appointmentService.arrival(id);
        return AjaxResult.success();
    }

    //编辑
    @RequiresPermissions("appointment:edit")
    @RequestMapping("/edit")
    @ResponseBody
    public AjaxResult edit(Appointment appointment){
        appointmentService.update(appointment);
        return AjaxResult.success();
    }

    //删除
    @RequiresPermissions("appointment:remove")
    @RequestMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids){
        appointmentService.deleteBatch(ids);
        return AjaxResult.success();
    }


}
