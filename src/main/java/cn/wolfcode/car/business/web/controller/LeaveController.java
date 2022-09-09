package cn.wolfcode.car.business.web.controller;


import cn.wolfcode.car.base.service.IUserService;
import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.domain.Leave;
import cn.wolfcode.car.business.query.LeaveQuery;
import cn.wolfcode.car.business.serivce.IBpmnInfoService;
import cn.wolfcode.car.business.serivce.ILeaveService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.web.AjaxResult;
import cn.wolfcode.car.shiro.ShiroUtils;
import org.apache.poi.util.IOUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * 套餐审核控制器
 */
@Controller
@RequestMapping("business/leave")
public class LeaveController {
    //模板前缀
    private static final String prefix = "business/leave/";

    @Autowired
    private ILeaveService leaveService;

    @Autowired
    private IBpmnInfoService bpmnInfoService;

    @Autowired
    private IUserService userService;

    //页面------------------------------------------------------------
    //列表
    @RequiresPermissions("leave:view")
    @RequestMapping("/listPage")
    public String listPage(){
        return prefix + "list";
    }

    @RequiresPermissions("leave:add")
    @RequestMapping("/addPage")
    public String addPage(){
        return prefix + "add";
    }


    @RequiresPermissions("leave:edit")
    @RequestMapping("/editPage")
    public String editPage(Long id, Model model){
        model.addAttribute("leave", leaveService.get(id));
        return prefix + "edit";
    }

    // @RequiresPermissions("leave:view")
    @RequestMapping("/todoPage")
    public String todoPage(){
        return prefix + "todoPage";
    }


    // id: 1
    //director: 3
    //auditStatus: 2
    //info: 123


    // 审核流程控制
    @RequestMapping("/startAudit")
    @ResponseBody
    public AjaxResult startAudit(Long id,Long bpmnInfoId,Long director,Long finance,String info){
        // 开始审计
        leaveService.startAudit(id,bpmnInfoId,director,finance,info);
        System.out.println(bpmnInfoId);
        return AjaxResult.success();
    }



    @RequestMapping("/auditPage")
    public String auditPage(Long id,Model model){
        model.addAttribute("id",id);
        // 店长 directors
        model.addAttribute("directors",userService.queryByRoleKey("shopOwner"));
        // 请假流程
        BpmnInfo bpmnInfo = bpmnInfoService.queryByBpmnType("car_leave");
        model.addAttribute("bpmnInfo",bpmnInfo);

        return prefix + "auditPage";
    }



    //数据-----------------------------------------------------------
    //列表
    @RequiresPermissions("leave:list")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<Leave> query(LeaveQuery qo){

        return leaveService.query(qo);
    }


    //新增
    @RequiresPermissions("leave:add")
    @RequestMapping("/add")
    @ResponseBody
    public AjaxResult addSave(Leave leave){
        leaveService.save(leave);
        return AjaxResult.success();
    }



    //编辑
    @RequiresPermissions("leave:edit")
    @RequestMapping("/edit")
    @ResponseBody
    public AjaxResult edit(Leave leave){
        leaveService.update(leave);
        return AjaxResult.success();
    }




}
