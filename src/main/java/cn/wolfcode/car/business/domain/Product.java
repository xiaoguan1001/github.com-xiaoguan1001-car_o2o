package cn.wolfcode.car.business.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Setter
@Getter
@ToString
public class Product {
    private String name;
    private BigDecimal price;// 价格
}
