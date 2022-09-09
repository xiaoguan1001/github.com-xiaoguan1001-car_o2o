package cn.wolfcode.car.business.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Setter
@Getter
@ToString
public class Brand {
    private String name;
    private BigDecimal price;
}
