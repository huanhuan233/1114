package com.processagent.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sys_org")
@Getter
@Setter
public class OrgEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String name;

    /** 部门简称 */
    @Column(name = "short_name", length = 40)
    private String shortName;

    @Column(length = 40)
    private String code;

    /** 部门类型：公司、部门、科室等 */
    @Column(name = "org_type", length = 20)
    private String orgType;

    @Column(name = "parent_id")
    private Long parentId;

    /** 部门人数 */
    @Column(name = "head_count")
    private Integer headCount;

    @Column(name = "sort_order")
    private Integer sortOrder;
}
