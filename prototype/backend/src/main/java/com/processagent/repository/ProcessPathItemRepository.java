package com.processagent.repository;

import com.processagent.entity.ProcessPathItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessPathItemRepository extends JpaRepository<ProcessPathItem, Long> {

    List<ProcessPathItem> findByPartNameContaining(String partName);

    List<ProcessPathItem> findByStatus(String status);
}
