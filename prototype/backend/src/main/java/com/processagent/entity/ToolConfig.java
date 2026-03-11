package com.processagent.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "tool_config")
@Getter
@Setter
public class ToolConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    /**
     * mcp / api / custom
     */
    @Column(nullable = false, length = 20)
    private String type;

    @Column(length = 600)
    private String endpoint;

    /**
     * 已启用 / 已停用
     */
    @Column(length = 20)
    private String status;

    @Column(length = 500)
    private String description;

    @Column(name = "update_time")
    private Instant updateTime;
}

