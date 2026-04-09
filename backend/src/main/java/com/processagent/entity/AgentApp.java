package com.processagent.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "agent_app")
@Getter
@Setter
public class AgentApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 800)
    private String description;

    /**
     * workflow / chatflow / agent / chat / text
     */
    @Column(name = "app_type", length = 30)
    private String appType;

    /**
     * 草稿 / 已发布
     */
    @Column(length = 20)
    private String status;

    @Column(name = "public_url", length = 600)
    private String publicUrl;

    @Column(name = "workflow_json", columnDefinition = "LONGTEXT")
    private String workflowJson;

    @Column(name = "create_time")
    private Instant createTime;

    @Column(name = "update_time")
    private Instant updateTime;

    @Column(name = "publish_time")
    private Instant publishTime;
}

