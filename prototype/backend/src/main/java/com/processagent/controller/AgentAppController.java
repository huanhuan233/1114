package com.processagent.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.processagent.entity.AgentApp;
import com.processagent.repository.AgentAppRepository;
import com.processagent.util.ApiResponse;
import com.processagent.util.TimeFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/apps")
@RequiredArgsConstructor
public class AgentAppController {

    private final AgentAppRepository repository;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {
        List<AgentApp> list = repository.findAll();
        if (status != null && !status.isBlank()) {
            list = list.stream().filter(a -> status.equals(a.getStatus())).toList();
        }
        if (type != null && !type.isBlank()) {
            list = list.stream().filter(a -> type.equals(a.getAppType())).toList();
        }
        if (keyword != null && !keyword.isBlank()) {
            String k = keyword.toLowerCase();
            list = list.stream().filter(a ->
                    (a.getName() != null && a.getName().toLowerCase().contains(k))
                            || (a.getDescription() != null && a.getDescription().toLowerCase().contains(k))
                            || (a.getId() != null && String.valueOf(a.getId()).contains(k))
            ).toList();
        }
        List<Map<String, Object>> data = list.stream().map(this::toMap).collect(Collectors.toList());
        return ApiResponse.ok(data);
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> get(@PathVariable Long id) {
        return repository.findById(id)
                .map(this::toMapWithWorkflow)
                .map(ApiResponse::ok)
                .orElse(ApiResponse.fail("未找到"));
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
        AgentApp e = new AgentApp();
        e.setName(body.get("name") != null ? body.get("name").toString() : "未命名应用");
        e.setDescription(body.get("desc") != null ? body.get("desc").toString() : (body.get("description") != null ? body.get("description").toString() : ""));
        e.setAppType(body.get("type") != null ? body.get("type").toString() : "chatflow");
        e.setStatus("草稿");
        Instant now = Instant.now();
        e.setCreateTime(now);
        e.setUpdateTime(now);
        e.setWorkflowJson(body.get("workflowJson") != null ? body.get("workflowJson").toString() : "");
        e = repository.save(e);
        return ApiResponse.ok(toMapWithWorkflow(e));
    }

    @PutMapping("/{id}")
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        AgentApp e = repository.findById(id).orElse(null);
        if (e == null) return ApiResponse.fail("未找到");
        if (body.containsKey("name")) e.setName(body.get("name") != null ? body.get("name").toString() : e.getName());
        if (body.containsKey("desc")) e.setDescription(body.get("desc") != null ? body.get("desc").toString() : e.getDescription());
        if (body.containsKey("description")) e.setDescription(body.get("description") != null ? body.get("description").toString() : e.getDescription());
        if (body.containsKey("type")) e.setAppType(body.get("type") != null ? body.get("type").toString() : e.getAppType());
        e.setUpdateTime(Instant.now());
        e = repository.save(e);
        return ApiResponse.ok(toMapWithWorkflow(e));
    }

    @PutMapping("/{id}/workflow")
    public ApiResponse<Map<String, Object>> saveWorkflow(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        AgentApp e = repository.findById(id).orElse(null);
        if (e == null) return ApiResponse.fail("未找到");
        String workflowJson = null;
        if (body.get("workflowJson") != null) {
            workflowJson = body.get("workflowJson").toString();
        } else if (body.get("nodes") != null) {
            try {
                workflowJson = objectMapper.writeValueAsString(Map.of("nodes", body.get("nodes")));
            } catch (JsonProcessingException ex) {
                workflowJson = "";
            }
        }
        if (workflowJson == null) workflowJson = "";
        e.setWorkflowJson(workflowJson);
        e.setUpdateTime(Instant.now());
        e = repository.save(e);
        return ApiResponse.ok(toMapWithWorkflow(e));
    }

    @PostMapping("/{id}/publish")
    public ApiResponse<Map<String, Object>> publish(@PathVariable Long id) {
        AgentApp e = repository.findById(id).orElse(null);
        if (e == null) return ApiResponse.fail("未找到");
        e.setStatus("已发布");
        e.setPublishTime(Instant.now());
        if (e.getPublicUrl() == null || e.getPublicUrl().isBlank()) {
            e.setPublicUrl("/public/apps/" + e.getId());
        }
        e.setUpdateTime(Instant.now());
        e = repository.save(e);
        return ApiResponse.ok(toMapWithWorkflow(e));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) return ApiResponse.fail("未找到");
        repository.deleteById(id);
        return ApiResponse.ok(null);
    }

    private Map<String, Object> toMap(AgentApp e) {
        return Map.of(
                "id", String.valueOf(e.getId()),
                "name", nullToEmpty(e.getName()),
                "desc", nullToEmpty(e.getDescription()),
                "type", nullToEmpty(e.getAppType()),
                "status", nullToEmpty(e.getStatus()),
                "publicUrl", nullToEmpty(e.getPublicUrl()),
                "createTime", e.getCreateTime() != null ? TimeFormat.format(e.getCreateTime()) : "",
                "updateTime", e.getUpdateTime() != null ? TimeFormat.format(e.getUpdateTime()) : "",
                "publishTime", e.getPublishTime() != null ? TimeFormat.format(e.getPublishTime()) : ""
        );
    }

    private Map<String, Object> toMapWithWorkflow(AgentApp e) {
        return new java.util.HashMap<String, Object>() {{
            putAll(toMap(e));
            put("workflowJson", nullToEmpty(e.getWorkflowJson()));
        }};
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}

