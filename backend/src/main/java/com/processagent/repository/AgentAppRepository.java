package com.processagent.repository;

import com.processagent.entity.AgentApp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgentAppRepository extends JpaRepository<AgentApp, Long> {
}

