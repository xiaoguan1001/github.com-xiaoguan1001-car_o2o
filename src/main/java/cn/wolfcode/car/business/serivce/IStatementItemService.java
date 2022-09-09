package cn.wolfcode.car.business.serivce;


import cn.wolfcode.car.business.domain.StatementItem;
import cn.wolfcode.car.business.query.StatementItemQuery;
import cn.wolfcode.car.common.base.page.TablePageInfo;

import java.util.List;

/**
 * 岗位服务接口
 */
public interface IStatementItemService {

    /**
     * 分页
     * @param qo
     * @return
     */
    TablePageInfo<StatementItem> query(StatementItemQuery qo);


    /**
     * 查单个
     * @param id
     * @return
     */
    StatementItem get(Long id);


    /**
     * 保存
     * @param statementItem
     */
    void save(StatementItem statementItem);

  
    /**
     * 更新
     * @param statementItem
     */
    void update(StatementItem statementItem);

    /**
     *  批量删除
     * @param ids
     */
    void deleteBatch(String ids);

    /**
     * 查询全部
     * @return
     */
    List<StatementItem> list();

    /**
     * 添加服务项集合
     * @param statementItems
     */
    void insertStatementItems(List<StatementItem> statementItems);

    /**
     * 用户上架操作
     * @param statementId
     */
    void payStatement(Long statementId);
}
