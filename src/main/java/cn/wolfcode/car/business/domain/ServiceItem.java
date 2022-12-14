package cn.wolfcode.car.business.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Setter
@Getter
public class ServiceItem {

    private static final long serialVersionUID = 1L;
    public static final Integer CARPACKAGE_NO = 0;//不是套餐
    public static final Integer CARPACKAGE_YES = 1;//是套餐
    public static final Integer AUDITSTATUS_INIT = 0;//初始化
    public static final Integer AUDITSTATUS_AUDITING = 1;//审核中
    public static final Integer AUDITSTATUS_APPROVED = 2;//审核通过
    public static final Integer AUDITSTATUS_REPLY = 3;//重新调整
    public static final Integer AUDITSTATUS_NO_REQUIRED = 4;//无需审核
    public static final Integer SALESTATUS_OFF = 0;//下架
    public static final Integer SALESTATUS_ON = 1;//上架


    /** */
    private Long id;

    /** 服务项名称*/
    private String name;

    /** 服务项原价*/
    private BigDecimal originalPrice;

    /** 服务项折扣价*/
    private BigDecimal discountPrice;

    /** 是否套餐【是/否】*/
    private Integer carPackage;

    /** 备注信息*/
    private String info;

    /** 创建时间*/
    private Date createTime;

    /** 服务分类【维修/保养/其他】*/
    private Integer serviceCatalog;

    /** 审核状态【初始化/审核中/审核通过/审核拒绝/无需审核】*/
    private Integer auditStatus;

    /** 上架状态【已上架/未上架】*/
    private Integer saleStatus;

}