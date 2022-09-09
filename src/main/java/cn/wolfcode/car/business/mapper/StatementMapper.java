package cn.wolfcode.car.business.mapper;

import cn.wolfcode.car.business.domain.Statement;
import cn.wolfcode.car.business.query.StatementQuery;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface StatementMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Statement record);

    Statement selectByPrimaryKey(Long id);

    List<Statement> selectAll();

    int updateByPrimaryKey(Statement record);

    List<Statement> selectForList(StatementQuery qo);

    void updateByStatus(Statement statement);

    /**
     * 顾客到店操作
     * @param statement 封装了用户所有的服务项记录
     */
    void updateStatementItem(Statement statement);


    /**
     *
     * @param statementId //订单id
     * @param totalPrice    // 总价格
     * @param totalQuantity // 总数量
     * @param bigDecimal    // 优惠价格
     */
    void updateStatementItemNull(
            @Param("statementId") Long statementId,
            @Param("totalPrice") BigDecimal totalPrice,
            @Param("totalQuantity") BigDecimal totalQuantity,
            @Param("bigDecimal") BigDecimal bigDecimal);

    /**
     * 修改支付
     * @param statement
     */
    void payItemAmount(Statement statement);

    /**
     * 根据预约单的id值查询信息
     * @param id
     * @return
     */
    Statement selectByAppointmentId(Long id);

    /**
     * 添加预约订单的内容
     * @param statement
     */
    void saveAppointmen(Statement statement);
}