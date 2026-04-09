package com.processagent.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "sys_user")
@Getter
@Setter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 工号 */
    @Column(name = "employee_no", length = 40)
    private String employeeNo;

    /** 姓名 */
    @Column(name = "real_name", length = 40)
    private String realName;

    @Column(nullable = false, unique = true, length = 60)
    private String username;

    /** 人员类别 */
    @Column(name = "personnel_category", length = 40)
    private String personnelCategory;

    /** 岗位类型 */
    @Column(name = "position_type", length = 40)
    private String positionType;

    /** 职务 */
    @Column(name = "job_title", length = 60)
    private String jobTitle;

    @Column(length = 80)
    private String dept;

    @Column(name = "role_id")
    private Long roleId;

    /** 联系方式 */
    @Column(length = 80)
    private String contact;

    @Column(length = 20)
    private String status;

    @Column(name = "last_login")
    private Instant lastLogin;

    /** 登陆时间（当前或最近一次会话） */
    @Column(name = "login_time")
    private Instant loginTime;

    /** 登出时间（当前或最近一次会话） */
    @Column(name = "logout_time")
    private Instant logoutTime;
}
