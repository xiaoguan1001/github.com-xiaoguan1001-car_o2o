package cn.wolfcode.car.business.web.controller;


import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.query.StatementQuery;
import cn.wolfcode.car.business.serivce.IStatementService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.web.AjaxResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 服务结算单控制器
 */
@Controller
@RequestMapping("system/statement")
public class StatementController {
    //模板前缀
    private static final String prefix = "business/statement/";

    @Autowired
    private IStatementService statementService;

    //页面------------------------------------------------------------
    //列表
    @RequiresPermissions("statement:view")
    @RequestMapping("/listPage")
    public String listPage(){
        return prefix + "list";
    }

    @RequiresPermissions("statement:add")
    @RequestMapping("/addPage")
    public String addPage(){
        return prefix + "add";
    }


    @RequiresPermissions("statement:edit")
    @RequestMapping("/editPage")
    public String editPage(Long id, Model model){
        model.addAttribute("statement", statementService.get(id));
        return prefix + "edit";
    }

    //数据-----------------------------------------------------------
    //列表
    @RequiresPermissions("statement:list")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<Statement> query(StatementQuery qo){
        return statementService.query(qo);
    }



    //新增
    @RequiresPermissions("statement:add")
    @RequestMapping("/add")
    @ResponseBody
    public AjaxResult addSave(Statement statement){
        statementService.save(statement);
        return AjaxResult.success();
    }


    //编辑
    @RequiresPermissions("statement:edit")
    @RequestMapping("/edit")
    @ResponseBody
    public AjaxResult edit(Statement statement){
        statementService.update(statement);
        return AjaxResult.success();
    }



    //删除
    @RequiresPermissions("statement:remove")
    @RequestMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids){
        statementService.deleteBatch(ids);
        return AjaxResult.success();
    }

    @RequiresPermissions("statement:delete")
    @RequestMapping("/delete")
    @ResponseBody
    public AjaxResult delete(Long id){
        // 调用业务方法对服务结算单的内容进行删除
        statementService.deleteRelation(id);

        return AjaxResult.success();
    }


    // 产品的查看结算单操作
    @RequiresPermissions("statement:itemDetail")
    @RequestMapping("/itemDetail")
    public String itemDetail(Long id){
        // 查询出服务结算单的id值

        // 当预约单状态为：结算单生成，那么跳转到页面是结算单明细查看页面(statementItem/showDetail.html)

        // 当预约单状态为：已到店，那么跳转到页面是结算单明细编辑页面(statementItem/editDetail.html)
        Long statementId = statementService.itemDetail(id);
        return "redirect:/business/statementItem/listPage?statementId="+statementId;
    }


}
