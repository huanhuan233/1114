package com.processagent.controller;

import com.processagent.entity.RoleEntity;
import com.processagent.entity.RolePermissionEntity;
import com.processagent.entity.UserEntity;
import com.processagent.entity.UserSession;
import com.processagent.repository.RolePermissionRepository;
import com.processagent.repository.RoleRepository;
import com.processagent.repository.UserRepository;
import com.processagent.repository.UserSessionRepository;
import com.processagent.util.ApiResponse;
import com.processagent.util.TimeFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/permission")
@RequiredArgsConstructor
public class PermissionController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserSessionRepository userSessionRepository;

    @GetMapping("/users")
    public ApiResponse<List<Map<String, Object>>> listUsers() {
        List<UserEntity> list = userRepository.findAll();
        List<RoleEntity> roles = roleRepository.findAll();
        Map<Long, String> roleNameMap = roles.stream().collect(Collectors.toMap(RoleEntity::getId, RoleEntity::getName, (a, b) -> a));
        List<Map<String, Object>> data = list.stream().map(u -> toUserMap(u, roleNameMap.get(u.getRoleId()))).collect(Collectors.toList());
        return ApiResponse.ok(data);
    }

    @GetMapping("/users/{id}")
    public ApiResponse<Map<String, Object>> getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(u -> toUserMap(u, roleRepository.findById(u.getRoleId()).map(RoleEntity::getName).orElse("")))
                .map(ApiResponse::ok)
                .orElse(ApiResponse.fail("未找到"));
    }

    @GetMapping("/roles")
    public ApiResponse<List<Map<String, Object>>> listRoles() {
        List<Map<String, Object>> data = roleRepository.findAll().stream().map(this::toRoleMap).collect(Collectors.toList());
        return ApiResponse.ok(data);
    }

    @GetMapping("/roles/{id}")
    public ApiResponse<Map<String, Object>> getRole(@PathVariable Long id) {
        return roleRepository.findById(id)
                .map(this::toRoleMap)
                .map(ApiResponse::ok)
                .orElse(ApiResponse.fail("未找到"));
    }

    @GetMapping("/menu-tree")
    public ApiResponse<List<Map<String, Object>>> getMenuTree() {
        List<Map<String, Object>> tree = List.of(
                Map.of("id", "1", "name", "工艺知识库", "children", List.of(
                        Map.of("id", "1-1", "name", "查看"),
                        Map.of("id", "1-2", "name", "新增/编辑"),
                        Map.of("id", "1-3", "name", "删除"))),
                Map.of("id", "2", "name", "工艺设计过程", "children", List.of(
                        Map.of("id", "2-1", "name", "查看任务"),
                        Map.of("id", "2-2", "name", "新建任务"),
                        Map.of("id", "2-3", "name", "取消/重试"))),
                Map.of("id", "3", "name", "工艺路径输出", "children", List.of(
                        Map.of("id", "3-1", "name", "查看"),
                        Map.of("id", "3-2", "name", "生成工艺路径"),
                        Map.of("id", "3-3", "name", "导出"))),
                Map.of("id", "4", "name", "系统管理", "children", List.of(
                        Map.of("id", "4-1", "name", "用户管理"),
                        Map.of("id", "4-2", "name", "角色管理"),
                        Map.of("id", "4-3", "name", "权限配置"),
                        Map.of("id", "4-4", "name", "组织机构"),
                        Map.of("id", "4-5", "name", "日志追踪")))
        );
        return ApiResponse.ok(tree);
    }

    @GetMapping("/roles/{id}/permissions")
    public ApiResponse<List<String>> getRolePermissions(@PathVariable Long id) {
        if (!roleRepository.existsById(id)) return ApiResponse.fail("未找到");
        List<String> ids = rolePermissionRepository.findByRoleId(id).stream()
                .map(RolePermissionEntity::getPermissionId)
                .collect(Collectors.toList());
        return ApiResponse.ok(ids);
    }

    @PutMapping("/roles/{id}/permissions")
    public ApiResponse<Void> saveRolePermissions(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        RoleEntity role = roleRepository.findById(id).orElse(null);
        if (role == null) return ApiResponse.fail("未找到");
        @SuppressWarnings("unchecked")
        List<String> permissionIds = (List<String>) body.get("permissionIds");
        if (permissionIds == null) permissionIds = List.of();
        rolePermissionRepository.deleteByRoleId(id);
        for (String pid : permissionIds) {
            RolePermissionEntity rp = new RolePermissionEntity();
            rp.setRoleId(id);
            rp.setPermissionId(pid);
            rolePermissionRepository.save(rp);
        }
        role.setPermissionCount(permissionIds.size());
        roleRepository.save(role);
        return ApiResponse.ok(null);
    }

    @PostMapping("/users")
    public ApiResponse<Map<String, Object>> createUser(@RequestBody Map<String, Object> body) {
        UserEntity e = new UserEntity();
        e.setEmployeeNo((String) body.get("employeeNo"));
        e.setRealName((String) body.get("realName"));
        e.setUsername(body.get("username") != null ? body.get("username").toString() : "");
        e.setPersonnelCategory((String) body.get("personnelCategory"));
        e.setPositionType((String) body.get("positionType"));
        e.setJobTitle((String) body.get("jobTitle"));
        e.setDept((String) body.get("dept"));
        e.setRoleId(body.get("roleId") != null ? ((Number) body.get("roleId")).longValue() : null);
        e.setContact((String) body.get("contact"));
        e.setStatus(body.get("status") != null ? body.get("status").toString() : "正常");
        e.setLastLogin(null);
        e.setLoginTime(null);
        e.setLogoutTime(null);
        e = userRepository.save(e);
        String roleName = e.getRoleId() != null ? roleRepository.findById(e.getRoleId()).map(RoleEntity::getName).orElse("") : "";
        return ApiResponse.ok(toUserMap(e, roleName));
    }

    @PutMapping("/users/{id}")
    public ApiResponse<Map<String, Object>> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        UserEntity e = userRepository.findById(id).orElse(null);
        if (e == null) return ApiResponse.fail("未找到");
        if (body.containsKey("employeeNo")) e.setEmployeeNo((String) body.get("employeeNo"));
        if (body.containsKey("realName")) e.setRealName((String) body.get("realName"));
        if (body.containsKey("username")) e.setUsername(body.get("username").toString());
        if (body.containsKey("personnelCategory")) e.setPersonnelCategory((String) body.get("personnelCategory"));
        if (body.containsKey("positionType")) e.setPositionType((String) body.get("positionType"));
        if (body.containsKey("jobTitle")) e.setJobTitle((String) body.get("jobTitle"));
        if (body.containsKey("dept")) e.setDept((String) body.get("dept"));
        if (body.containsKey("roleId")) e.setRoleId(body.get("roleId") != null ? ((Number) body.get("roleId")).longValue() : null);
        if (body.containsKey("contact")) e.setContact((String) body.get("contact"));
        if (body.containsKey("status")) e.setStatus(body.get("status").toString());
        if (body.containsKey("loginTime")) {
            Object v = body.get("loginTime");
            e.setLoginTime(v != null && !v.toString().isBlank() ? Instant.parse(v.toString()) : null);
        }
        if (body.containsKey("logoutTime")) {
            Object v = body.get("logoutTime");
            e.setLogoutTime(v != null && !v.toString().isBlank() ? Instant.parse(v.toString()) : null);
        }
        e = userRepository.save(e);
        String roleName = e.getRoleId() != null ? roleRepository.findById(e.getRoleId()).map(RoleEntity::getName).orElse("") : "";
        return ApiResponse.ok(toUserMap(e, roleName));
    }

    @PostMapping("/roles")
    public ApiResponse<Map<String, Object>> createRole(@RequestBody Map<String, Object> body) {
        RoleEntity e = new RoleEntity();
        e.setName((String) body.get("name"));
        e.setCode((String) body.get("code"));
        e.setDescription((String) body.get("desc"));
        e.setPermissionCount(body.get("permissionCount") != null ? ((Number) body.get("permissionCount")).intValue() : 0);
        e = roleRepository.save(e);
        return ApiResponse.ok(toRoleMap(e));
    }

    @PutMapping("/roles/{id}")
    public ApiResponse<Map<String, Object>> updateRole(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        RoleEntity e = roleRepository.findById(id).orElse(null);
        if (e == null) return ApiResponse.fail("未找到");
        if (body.containsKey("name")) e.setName((String) body.get("name"));
        if (body.containsKey("code")) e.setCode((String) body.get("code"));
        if (body.containsKey("desc")) e.setDescription((String) body.get("desc"));
        if (body.containsKey("permissionCount")) e.setPermissionCount(((Number) body.get("permissionCount")).intValue());
        e = roleRepository.save(e);
        return ApiResponse.ok(toRoleMap(e));
    }

    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) return ApiResponse.fail("未找到");
        userRepository.deleteById(id);
        return ApiResponse.ok(null);
    }

    @DeleteMapping("/roles/{id}")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        if (!roleRepository.existsById(id)) return ApiResponse.fail("未找到");
        rolePermissionRepository.deleteByRoleId(id);
        roleRepository.deleteById(id);
        return ApiResponse.ok(null);
    }

    /** 日志统计：按时间范围返回总计运行时间（秒）、系统实际运行时间（秒）。可选参数 startTime,endTime 为 ISO-8601 或 yyyy-MM-dd（按日统计）。 */
    @GetMapping("/log-stats")
    public ApiResponse<Map<String, Object>> getLogStats(
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        Instant now = Instant.now();
        java.time.ZoneId zone = java.time.ZoneId.of("Asia/Shanghai");
        Instant rangeStart = null;
        Instant rangeEnd = null;
        if (startTime != null && !startTime.isBlank() && endTime != null && !endTime.isBlank()) {
            try {
                if (startTime.length() <= 10) {
                    rangeStart = java.time.LocalDate.parse(startTime).atStartOfDay(zone).toInstant();
                    rangeEnd = java.time.LocalDate.parse(endTime).plusDays(1).atStartOfDay(zone).toInstant();
                } else {
                    rangeStart = Instant.parse(startTime);
                    rangeEnd = Instant.parse(endTime);
                }
            } catch (Exception ignored) {
                rangeStart = null;
                rangeEnd = null;
            }
        }
        List<UserSession> sessions = rangeStart != null && rangeEnd != null
                ? userSessionRepository.findSessionsOverlapping(rangeStart, rangeEnd)
                : userSessionRepository.findAll();
        long totalRunTimeSeconds = 0;
        List<long[]> intervals = new ArrayList<>();
        for (UserSession s : sessions) {
            Instant sessionEnd = s.getLogoutAt() != null ? s.getLogoutAt() : now;
            long startEpoch = s.getLoginAt().getEpochSecond();
            long endEpoch = sessionEnd.getEpochSecond();
            if (rangeStart != null && rangeEnd != null) {
                long rangeStartEpoch = rangeStart.getEpochSecond();
                long rangeEndEpoch = rangeEnd.getEpochSecond();
                startEpoch = Math.max(startEpoch, rangeStartEpoch);
                endEpoch = Math.min(endEpoch, rangeEndEpoch);
            }
            if (endEpoch > startEpoch) {
                totalRunTimeSeconds += (endEpoch - startEpoch);
                intervals.add(new long[] { startEpoch, endEpoch });
            }
        }
        intervals.sort(Comparator.comparingLong(a -> a[0]));
        long systemActualRunTimeSeconds = 0;
        long lastEnd = Long.MIN_VALUE;
        for (long[] iv : intervals) {
            long start = iv[0], end = iv[1];
            if (start > lastEnd) {
                systemActualRunTimeSeconds += (end - start);
                lastEnd = end;
            } else if (end > lastEnd) {
                systemActualRunTimeSeconds += (end - lastEnd);
                lastEnd = end;
            }
        }
        Map<String, Object> data = new HashMap<>();
        data.put("totalRunTimeSeconds", totalRunTimeSeconds);
        data.put("systemActualRunTimeSeconds", systemActualRunTimeSeconds);
        if (rangeStart != null && rangeEnd != null) {
            data.put("startTime", startTime);
            data.put("endTime", endTime);
        }
        return ApiResponse.ok(data);
    }

    /** 记录登陆：创建会话并更新用户的 loginTime */
    @PostMapping("/session")
    public ApiResponse<Map<String, Object>> recordLogin(@RequestBody Map<String, Object> body) {
        Long userId = body.get("userId") != null ? ((Number) body.get("userId")).longValue() : null;
        if (userId == null) return ApiResponse.fail("缺少 userId");
        if (!userRepository.existsById(userId)) return ApiResponse.fail("用户不存在");
        Instant at = Instant.now();
        UserSession session = new UserSession();
        session.setUserId(userId);
        session.setLoginAt(at);
        session.setLogoutAt(null);
        session = userSessionRepository.save(session);
        userRepository.findById(userId).ifPresent(u -> {
            u.setLoginTime(at);
            u.setLastLogin(at);
            userRepository.save(u);
        });
        Map<String, Object> data = new HashMap<>();
        data.put("id", session.getId());
        data.put("userId", session.getUserId());
        data.put("loginAt", TimeFormat.format(session.getLoginAt()));
        return ApiResponse.ok(data);
    }

    /** 记录登出：更新会话的 logoutAt 和用户的 logoutTime */
    @PutMapping("/session/{id}")
    public ApiResponse<Map<String, Object>> recordLogout(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        UserSession session = userSessionRepository.findById(id).orElse(null);
        if (session == null) return ApiResponse.fail("会话不存在");
        Instant at = body.get("logoutAt") != null && !body.get("logoutAt").toString().isBlank()
                ? Instant.parse(body.get("logoutAt").toString()) : Instant.now();
        session.setLogoutAt(at);
        userSessionRepository.save(session);
        userRepository.findById(session.getUserId()).ifPresent(u -> {
            u.setLogoutTime(at);
            userRepository.save(u);
        });
        Map<String, Object> data = new HashMap<>();
        data.put("id", session.getId());
        data.put("logoutAt", TimeFormat.format(at));
        return ApiResponse.ok(data);
    }

    private Map<String, Object> toUserMap(UserEntity u, String roleName) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", String.valueOf(u.getId()));
        m.put("employeeNo", nullToEmpty(u.getEmployeeNo()));
        m.put("realName", nullToEmpty(u.getRealName()));
        m.put("username", nullToEmpty(u.getUsername()));
        m.put("personnelCategory", nullToEmpty(u.getPersonnelCategory()));
        m.put("positionType", nullToEmpty(u.getPositionType()));
        m.put("jobTitle", nullToEmpty(u.getJobTitle()));
        m.put("dept", nullToEmpty(u.getDept()));
        m.put("role", nullToEmpty(roleName));
        m.put("roleId", u.getRoleId());
        m.put("contact", nullToEmpty(u.getContact()));
        m.put("status", nullToEmpty(u.getStatus()));
        m.put("lastLogin", u.getLastLogin() != null ? TimeFormat.format(u.getLastLogin()) : "");
        m.put("loginTime", u.getLoginTime() != null ? TimeFormat.format(u.getLoginTime()) : "");
        m.put("logoutTime", u.getLogoutTime() != null ? TimeFormat.format(u.getLogoutTime()) : "");
        return m;
    }

    private Map<String, Object> toRoleMap(RoleEntity e) {
        return Map.of(
                "id", String.valueOf(e.getId()),
                "name", nullToEmpty(e.getName()),
                "code", nullToEmpty(e.getCode()),
                "desc", nullToEmpty(e.getDescription()),
                "permissionCount", e.getPermissionCount() != null ? e.getPermissionCount() : 0
        );
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
