package cn.wolfcode.car.business.domain;

import cn.wolfcode.car.base.domain.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class Leave {

    public static final Integer AUDITSTATUS_INIT = 0;//初始化
    public static final Integer AUDITSTATUS_AUDITING = 1;//审核中
    public static final Integer AUDITSTATUS_APPROVED = 2;//审核通过
    public static final Integer AUDITSTATUS_REPLY = 3;//重新调整
    public static final Integer AUDITSTATUS_NO_REQUIRED = 4;//无需审核

    public static final Integer STATUS_IN_ROGRESS = 0;//审核中
    public static final Integer STATUS_REJECT = 1;//审核拒绝
    public static final Integer STATUS_PASS = 2;//审核通过
    public static final Integer STATUS_CANCEL = 3;//审核撤销
    private static final long serialVersionUID = 1L;

    /** */
    private Long id;

    /** */
    private String name;

    /** */
    private String reason;

    /** */
    @JsonFormat(pattern = "yyyy-MM-dd HH:ss")
    private Date starttime;

    /** */
    @JsonFormat(pattern = "yyyy-MM-dd HH:ss")
    private Date endtime;

    /** */
    private Integer auditid;

    private User auditor;                   //当前审核人对象

    /** */
    private String status;

    /** */
    private String info;


}