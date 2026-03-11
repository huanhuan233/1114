package com.processagent.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "integration_task")
@Getter
@Setter
public class IntegrationTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "task_system", length = 20)
    private String system;

    @Column(length = 20)
    private String status;

    private Integer progress;

    @Column(name = "create_time")
    private Instant createTime;

    /** 开始时间 */
    @Column(name = "start_time")
    private Instant startTime;

    /** 结束时间 */
    @Column(name = "end_time")
    private Instant endTime;

    /** 运行时间（秒） */
    @Column(name = "run_time_seconds")
    private Long runTimeSeconds;
}
