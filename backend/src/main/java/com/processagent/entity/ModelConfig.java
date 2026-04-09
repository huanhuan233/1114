package com.processagent.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "model_config")
@Getter
@Setter
public class ModelConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 40)
    private String type;

    @Column(length = 80)
    private String provider;

    @Column(length = 500)
    private String endpoint;

    @Column(name = "api_key", length = 500)
    private String apiKey;

    @Column(length = 500)
    private String remark;

    @Column(length = 20)
    private String status;

    @Column(name = "update_time")
    private Instant updateTime;
}
