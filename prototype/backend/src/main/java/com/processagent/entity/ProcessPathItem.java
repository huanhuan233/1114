package com.processagent.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "process_path_item")
@Getter
@Setter
public class ProcessPathItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "part_name", nullable = false, length = 120)
    private String partName;

    @Column(columnDefinition = "TEXT")
    private String path;

    private Integer steps;

    @Column(length = 20)
    private String status;

    @Column(name = "output_time")
    private Instant outputTime;
}
