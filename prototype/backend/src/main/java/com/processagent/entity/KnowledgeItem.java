package com.processagent.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "knowledge_item")
@Getter
@Setter
public class KnowledgeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 80)
    private String category;

    @Column(length = 80)
    private String type;

    @Column(length = 40)
    private String version;

    @Column(name = "update_time")
    private Instant updateTime;

    @Column(length = 20)
    private String status;
}
