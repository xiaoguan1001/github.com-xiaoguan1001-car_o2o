package cn.wolfcode.car.business.web.controller;


import cn.wolfcode.car.base.domain.User;
import cn.wolfcode.car.base.service.IUserService;
import cn.wolfcode.car.business.domain.BpmnInfo;
import cn.wolfcode.car.business.domain.ServiceItem;
import cn.wolfcode.car.business.query.ServiceItemQuery;
import cn.wolfcode.car.business.serivce.IBpmnInfoService;
import cn.wolfcode.car.business.serivce.IServiceItemService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BaseException;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.web.AjaxResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.aspectj.weaver.loadtime.Aj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.nio.BufferOverflowException;
import java.util.List;

/**
 * 服务项控制器
 */
@Controller
@RequestMapping("system/serviceItem")
public class ServiceItemController {
    //模板前缀
    private static final String prefix = "business/serviceItem/";

    @Autowired
    private IServiceItemService serviceItemService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IBpmnInfoService bpmnInfoService;

    //页面------------------------------------------------------------
    //列表
    @RequiresPermissions("serviceItem:view")
    @RequestMapping("/listPage")
    public String listPage(){
        return prefix + "list";
    }

    @RequiresPermissions("serviceItem:add")
    @RequestMapping("/addPage")
    public String addPage(){
        return prefix + "add";
    }




    @RequiresPermissions("serviceItem:edit")
    @RequestMapping("/editPage")
    public String editPage(Long id, Model model){
        model.addAttribute("serviceItem", serviceItemService.get(id));
        return prefix + "edit";
    }




    //服务套餐发起审核操作
    @RequestMapping("/auditPage")
    public String auditPage(Long id,Model model){

        if (id != null) {
            // 服务单项内容
            ServiceItem serviceItem = serviceItemService.get(id);
            model.addAttribute("serviceItem",serviceItem);
            // 审核流程
            BpmnInfo bpmnInfo = bpmnInfoService.queryByBpmnType("car_package");
            model.addAttribute("bpmnInfo",bpmnInfo);

            // 店长 directors
            model.addAttribute("directors",userService.queryByRoleKey("shopOwner"));
            // 财务 finances

            model.addAttribute("finances",userService.queryByRoleKey("caiwu"));

            return prefix+"auditPage";
        }

        throw new BusinessException("请选择一个流程审核套餐");
    }






    //数据-----------------------------------------------------------
    //列表
    @RequiresPermissions("serviceItem:list")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<ServiceItem> query(ServiceItemQuery qo){
        return serviceItemService.query(qo);
    }

    // 审核流程控制
    @RequestMapping("/startAudit")
    @ResponseBody
    public AjaxResult startAudit(Long id,Long bpmnInfoId,Long director,Long finance,String info){
        // 开始审计
        serviceItemService.startAudit(id,bpmnInfoId,director,finance,info);
        return AjaxResult.success();
    }

    //新增
    @RequiresPermissions("serviceItem:add")
    @RequestMapping("/add")
    @ResponseBody
    public AjaxResult addSave(ServiceItem serviceItem){
        serviceItemService.save(serviceItem);
        return AjaxResult.success();
    }





    //编辑
    @RequiresPermissions("serviceItem:edit")
    @RequestMapping("/edit")
    @ResponseBody
    public AjaxResult edit(ServiceItem serviceItem){
        serviceItemService.update(serviceItem);
        return AjaxResult.success();
    }

    //删除
    @RequiresPermissions("serviceItem:remove")
    @RequestMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids){
        serviceItemService.deleteBatch(ids);
        return AjaxResult.success();
    }

    // 上架
    @RequestMapping("/saleOff")
    @ResponseBody
    public AjaxResult saleOff(Long id){
        serviceItemService.saleOff(id);
        return AjaxResult.success();
    }

    // 上架
    @RequestMapping("/saleOn")
    @ResponseBody
    public AjaxResult saleOn(Long id){
        serviceItemService.saleOn(id);
        return AjaxResult.success();
    }


}
