package cn.wolfcode.car.business.web.controller;


import cn.wolfcode.car.base.domain.User;
import cn.wolfcode.car.base.service.IUserService;
import cn.wolfcode.car.business.domain.ServiceItem;
import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.domain.StatementItem;
import cn.wolfcode.car.business.query.ServiceItemQuery;
import cn.wolfcode.car.business.query.StatementItemQuery;
import cn.wolfcode.car.business.serivce.IServiceItemService;
import cn.wolfcode.car.business.serivce.IStatementItemService;
import cn.wolfcode.car.business.serivce.IStatementService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.web.AjaxResult;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 服务结算单明细控制器
 */
@Controller
@RequestMapping("business/statementItem")
public class StatementItemController {
    //模板前缀
    private static final String prefix = "business/statement/statementItem/";

    @Autowired
    private IStatementItemService statementItemService;
    @Autowired
    private IStatementService statementService;
    @Autowired
    private IServiceItemService serviceItemService;
    @Autowired
    private IUserService userService;

    //页面------------------------------------------------------------
    //列表
    @RequiresPermissions("statementItem:view")
    @RequestMapping("/listPage")
    public String listPage(Long statementId, Model model) {
        // 接收发送过来的请求参数控制界面的跳转
        Statement statement = statementService.get(statementId);
        model.addAttribute("statement", statement);
        // 控制跳转到所需的界面中
        if (Statement.STATUS_PAID.equals(statement.getStatus())) {
            // showDetail页面：当结算单位已支付
            // 收款人的id 查询出来
            User user = userService.get(statement.getPayeeId());
            // 将收款人封装到对象中
            statement.setPayee(user);

            return prefix + "showDetail";
        } else {
            // 消费中的界面
            return prefix + "editDetail";
        }
    }


    //数据-----------------------------------------------------------
    //列表
    @RequiresPermissions("statementItem:list")
    @RequestMapping("/query")
    @ResponseBody
    public TablePageInfo<StatementItem> query(StatementItemQuery qo) {
        // 只查询出该id下的所有的服务项目
        return statementItemService.query(qo);
    }


    // 添加服务项
    @RequiresPermissions("statementItem:list")
    @RequestMapping("/selectAllSaleOnList")
    @ResponseBody
    public TablePageInfo<ServiceItem> selectAllSaleOnList(ServiceItemQuery qo) {
        // 查询上架的所有商品
        qo.setSaleStatus(ServiceItem.SALESTATUS_ON);
        return serviceItemService.query(qo);
    }

    // 确认支付
    @RequiresPermissions("statementItem:list")
    @RequestMapping("/payStatement")
    @ResponseBody
    public AjaxResult payStatement(Long statementId) {
        // 支付成功操作
        statementItemService.payStatement(statementId);
        return AjaxResult.success();
    }


    // 接收浏览器中送过来的请求参数并对参数进行封装
    @RequiresPermissions("statement:saveItems")
    @RequestMapping("/saveItems")
    @ResponseBody
    public AjaxResult saveItems(@RequestBody List<StatementItem> statementItems) {
        // 需要使用 @RequestBody 注解进行接收前端传输过来的参数
        statementItemService.insertStatementItems(statementItems);
        return AjaxResult.success();
    }




}
