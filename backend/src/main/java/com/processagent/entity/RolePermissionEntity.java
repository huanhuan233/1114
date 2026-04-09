package com.processagent.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sys_role_permission", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "role_id", "permission_id" })
})
@Getter
@Setter
public class RolePermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "permission_id", nullable = false, length = 32)
    private String permissionId;
}
