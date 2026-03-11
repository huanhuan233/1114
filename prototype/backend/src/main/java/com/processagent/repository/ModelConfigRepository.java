package com.processagent.repository;

import com.processagent.entity.ModelConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModelConfigRepository extends JpaRepository<ModelConfig, Long> {

    List<ModelConfig> findByType(String type);

    List<ModelConfig> findByNameContainingIgnoreCase(String keyword);
}
