package com.processagent.repository;

import com.processagent.entity.KnowledgeItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface KnowledgeItemRepository extends JpaRepository<KnowledgeItem, Long>, JpaSpecificationExecutor<KnowledgeItem> {

    List<KnowledgeItem> findByCategory(String category);

    List<KnowledgeItem> findByNameContainingIgnoreCase(String keyword);
}
