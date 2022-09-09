package cn.wolfcode.car.business.serivce.impl;

import cn.wolfcode.car.base.domain.User;
import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.domain.StatementItem;
import cn.wolfcode.car.business.mapper.StatementItemMapper;
import cn.wolfcode.car.business.mapper.StatementMapper;
import cn.wolfcode.car.business.query.StatementItemQuery;
import cn.wolfcode.car.business.serivce.IStatementItemService;
import cn.wolfcode.car.common.base.page.TablePageInfo;
import cn.wolfcode.car.common.exception.BusinessException;
import cn.wolfcode.car.common.util.Convert;
import cn.wolfcode.car.shiro.ShiroUtils;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class StatementItemServiceImpl implements IStatementItemService {

    @Autowired
    private StatementItemMapper statementItemMapper;
    @Autowired
    private StatementMapper statementMapper;

    @Override
    public TablePageInfo<StatementItem> query(StatementItemQuery qo) {
        PageHelper.startPage(qo.getPageNum(), qo.getPageSize());

        return new TablePageInfo<StatementItem>(statementItemMapper.selectForList(qo));
    }


    @Override
    public void save(StatementItem statementItem) {
        statementItemMapper.insert(statementItem);
    }

    @Override
    public StatementItem get(Long id) {
        return statementItemMapper.selectByPrimaryKey(id);
    }


    @Override
    public void update(StatementItem statementItem) {

        statementItemMapper.updateByPrimaryKey(statementItem);
    }

    @Override
    public void deleteBatch(String ids) {
        Long[] dictIds = Convert.toLongArray(ids);
        for (Long dictId : dictIds) {
            // 跟改显示的状态
            statementItemMapper.deleteByPrimaryKey(dictId);
        }

    }

    @Override
    public List<StatementItem> list() {
        return statementItemMapper.selectAll();
    }


    public void insertStatementItems(List<StatementItem> statementItems) {
        // 总价格
        BigDecimal totalPrice = new BigDecimal("0.00");
        // 总数量
        BigDecimal totalQuantity = new BigDecimal("0.00");

        // 获取最后一优惠价格的元素
        StatementItem discountAmount = statementItems.remove(statementItems.size() - 1);
        // 只有消费中的状态才能进行保存明细
        Statement st = statementMapper.selectByPrimaryKey(discountAmount.getStatementId());
        if (! Statement.STATUS_CONSUME.equals(st.getStatus())) {
            throw new BusinessException("只有消费中的状态才能保存");
        }

        // 先删除表中的数据再进行添加
        Long statementId = discountAmount.getStatementId();
        statementItemMapper.deleteByStatementId(statementId);
        // 如果前端什么都传删除掉原本的数据
        statementMapper.updateStatementItemNull(statementId, totalPrice, totalQuantity, new BigDecimal("0"));

        if (statementId != null) {
            // 判空
            if (statementItems.size() > 0) {
                for (StatementItem statementItem : statementItems) {

                    statementItemMapper.insert(statementItem);
                    // 获取总价格
                    // 总数量
                    totalQuantity = totalQuantity.add(BigDecimal.valueOf(statementItem.getItemQuantity()));
                    // 总价 =  总价 + ( 单个价格 * 总数 )
                    totalPrice = totalPrice.add(statementItem.getItemPrice().multiply(BigDecimal.valueOf(statementItem.getItemQuantity())));


                }
            }
            Statement statement = statementMapper.selectByPrimaryKey(statementId);
            // 设置总金额
            statement.setTotalAmount(totalPrice);
            // 总数量
            statement.setTotalQuantity(totalQuantity);

            // 折扣金额
            statement.setDiscountAmount(discountAmount.getItemPrice());

            statementMapper.updateStatementItem(statement);
        }

    }


    public void payStatement(Long statementId) {
        Statement statement = statementMapper.selectByPrimaryKey(statementId);
        // 支付时间
        statement.setPayTime(new Date());
        // 收款的对象
        User user = ShiroUtils.getUser();
        statement.setPayee(user);
        // 收款人的ID
        statement.setPayeeId(user.getId());
        // 完成支付
        statement.setStatus(Statement.STATUS_PAID);
        statementMapper.payItemAmount(statement);
    }

}
