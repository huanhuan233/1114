package com.processagent.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "rule_config")
@Getter
@Setter
public class RuleConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "natural_language", length = 4000)
    private String naturalLanguage;

    @Column(name = "logic_output", columnDefinition = "TEXT")
    private String logicOutput;

    /**
     * 草稿 / 已启用 / 已停用
     */
    @Column(length = 20)
    private String status;

    @Column(name = "update_time")
    private Instant updateTime;
}

