package com.processagent.repository;

import com.processagent.entity.IntegrationTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IntegrationTaskRepository extends JpaRepository<IntegrationTask, Long> {

    List<IntegrationTask> findBySystem(String system);

    List<IntegrationTask> findByStatus(String status);
}
