package cn.wolfcode.car.business.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Setter
@Getter
public class Employee {

    public static final Integer ADMIN_YES = 1;//是超管
    public static final Integer ADMIN_OFF = 1;//不是超管


    public static final Integer STATUS_ON = 0;//解冻
    public static final Integer STATUS_OFF = 1;//冻结


    /** */
    private Long id;

    /** */
    private String name;

    /** */
    private String email;

    /** */
    private Integer age;

    /** */
    private Integer admin;

    /** */
    private Integer status;

    /** */
    private Department dept;

    /** */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date hiredate; // 入职时间

}