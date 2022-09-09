package cn.wolfcode.car.business.query;

import cn.wolfcode.car.common.base.query.QueryObject;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AppointmentQuery extends QueryObject {
    private String customerName;                    //客户姓名
    private String customerPhone;                   //客户联系方式
    private Integer status;                         //状态
    private final Integer conceal = 0;                       // 只显示0的内容

}
