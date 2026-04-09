package com.processagent.repository;

import com.processagent.entity.UserSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    List<UserSession> findByUserIdOrderByLoginAtDesc(Long userId, Pageable pageable);

    /** 查询在指定时间范围内有重叠的会话（loginAt < rangeEnd 且 (logoutAt 为空 或 logoutAt > rangeStart)） */
    @Query("SELECT s FROM UserSession s WHERE s.loginAt < :rangeEnd AND (s.logoutAt IS NULL OR s.logoutAt > :rangeStart)")
    List<UserSession> findSessionsOverlapping(@Param("rangeStart") Instant rangeStart, @Param("rangeEnd") Instant rangeEnd);
}
