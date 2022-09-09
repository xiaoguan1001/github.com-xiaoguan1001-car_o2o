package cn.wolfcode.car.business.web.controller;


import cn.wolfcode.car.business.domain.CarPackageAudit;
import cn.wolfcode.car.business.query.CarPackageAuditQuery;
import cn.wolfcode.car.business.serivce.ICarPackageAuditService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.web.AjaxResult;
import cn.wolfcode.car.shiro.ShiroUtils;
import org.apache.poi.ss.util.SheetUtil;
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
@RequestMapping("business/carPackageAudit")
public class CarPackageAuditController {
    //模板前缀
    private static final String prefix = "business/carPackageAudit/";

    @Autowired
    private ICarPackageAuditService carPackageAuditService;

    //页面------------------------------------------------------------
    //列表
    @RequiresPermissions("carPackageAudit:view")
    @RequestMapping("/listPage")
    public String listPage(){
        return prefix + "list";
    }

    @RequiresPermissions("carPackageAudit:add")
    @RequestMapping("/addPage")
    public String addPage(){
        return prefix + "add";
    }


    @RequiresPermissions("carPackageAudit:edit")
    @RequestMapping("/editPage")
    public String editPage(Long id, Model model){
        model.addAttribute("carPackageAudit", carPackageAuditService.get(id));
        return prefix + "edit";
    }

    // @RequiresPermissions("carPackageAudit:view")
    @RequestMapping("/todoPage")
    public String todoPage(){
        return prefix + "todoPage";
    }

    @RequestMapping("/auditPage")
    public String auditPage(Long id,Model model){
        model.addAttribute("id",id);
        return prefix + "auditPage";
    }

    @RequestMapping("/donePage")
    public String donePage(){
        return prefix + "donePage";
    }


    //数据-----------------------------------------------------------
    //列表
    @RequiresPermissions("carPackageAudit:list")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<CarPackageAudit> query(CarPackageAuditQuery qo){

        return carPackageAuditService.query(qo);
    }

    // 我的待办
    @RequestMapping("/todoQuery")
    @ResponseBody
    public TablePageInfo<CarPackageAudit> todoQuery(CarPackageAuditQuery qo){


        // 必须是审核中的状态
        qo.setStatus(CarPackageAudit.STATUS_IN_ROGRESS);
        // 登录的用户
        qo.setAuditorId(ShiroUtils.getUserId());
        // 已完成的状态

        return carPackageAuditService.query(qo);
    }

    // 我的已办
    @RequestMapping("/doneQuery")
    @ResponseBody
    public TablePageInfo<CarPackageAudit> doneQuery(CarPackageAuditQuery qo){
        // 过滤筛选的条件
        qo.setInfo(ShiroUtils.getUser().getUserName());

        return carPackageAuditService.query(qo);
    }



    //新增
    @RequiresPermissions("carPackageAudit:add")
    @RequestMapping("/add")
    @ResponseBody
    public AjaxResult addSave(CarPackageAudit carPackageAudit){
        carPackageAuditService.save(carPackageAudit);
        return AjaxResult.success();
    }

    // 流程审核
    @RequestMapping("/audit")
    @ResponseBody
    public AjaxResult audit(Long id,int auditStatus,String info){
        carPackageAuditService.audit(id,auditStatus,info);
        return AjaxResult.success();
    }


    //编辑
    @RequiresPermissions("carPackageAudit:edit")
    @RequestMapping("/edit")
    @ResponseBody
    public AjaxResult edit(CarPackageAudit carPackageAudit){
        carPackageAuditService.update(carPackageAudit);
        return AjaxResult.success();
    }



    // 进度查看
    @RequestMapping("/processImg")
    public void processImg(Long id, HttpServletResponse response) throws IOException {
        InputStream inputStream = carPackageAuditService.processImg(id);
        // 将查到的流程实列图返回到页面中进行展示
        IOUtils.copy(inputStream,response.getOutputStream());
    }

    //删除
    @RequiresPermissions("carPackageAudit:remove")
    @RequestMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids){
        carPackageAuditService.deleteBatch(ids);
        return AjaxResult.success();
    }


    // 审核撤销
    @RequestMapping("/cancelApply")
    @ResponseBody
    public AjaxResult cancelApply(Long id){
        carPackageAuditService.cancelApply(id);
        return AjaxResult.success();
    }


}
