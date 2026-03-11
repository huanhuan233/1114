package com.processagent.repository;

import com.processagent.entity.OrgEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrgRepository extends JpaRepository<OrgEntity, Long> {

    List<OrgEntity> findByParentIdOrderBySortOrderAsc(Long parentId);

    List<OrgEntity> findByParentIdIsNullOrderBySortOrderAsc();
}
