package com.processagent.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.processagent.entity.AgentApp;
import com.processagent.entity.IntegrationTask;
import com.processagent.repository.AgentAppRepository;
import com.processagent.repository.IntegrationTaskRepository;
import com.processagent.service.TaskWorkflowTemplateFactory;
import com.processagent.util.ApiResponse;
import com.processagent.util.TimeFormat;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/integration")
@RequiredArgsConstructor
public class IntegrationController {

    private static final Logger log = LoggerFactory.getLogger(IntegrationController.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final IntegrationTaskRepository repository;
    private final AgentAppRepository agentAppRepository;

    @GetMapping("/tasks")
    public ApiResponse<List<Map<String, Object>>> list(
            @RequestParam(required = false) String system,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String appPath) {
        List<IntegrationTask> list = repository.findAll();
        if (system != null && !system.isBlank()) {
            list = list.stream().filter(t -> system.equals(t.getSystem())).toList();
        }
        if (status != null && !status.isBlank()) {
            list = list.stream().filter(t -> status.equals(t.getStatus())).toList();
        }
        if (appPath != null && !appPath.isBlank()) {
            final String path = normalizeAppPath(appPath);
            list = list.stream()
                    .filter(t -> workflowContainsAppPath(getWorkflowJsonForTask(t), path))
                    .toList();
        }
        List<Map<String, Object>> data = list.stream().map(this::toMap).collect(Collectors.toList());
        return ApiResponse.ok(data);
    }

    /** 取任务对应工作流 JSON：优先用应用当前 workflow（已发布状态），否则用创建时快照。 */
    private String getWorkflowJsonForTask(IntegrationTask t) {
        if (t.getAppId() != null) {
            String fromApp = agentAppRepository.findById(t.getAppId())
                    .map(AgentApp::getWorkflowJson)
                    .orElse("");
            if (fromApp != null && !fromApp.isBlank()) return fromApp;
        }
        if (t.getWorkflowJsonSnapshot() != null && !t.getWorkflowJsonSnapshot().isBlank()) {
            return t.getWorkflowJsonSnapshot();
        }
        return "";
    }

    /** 统一路径格式：去首尾空格，保证以 / 开头便于与节点 appPath 比对。 */
    private static String normalizeAppPath(String appPath) {
        if (appPath == null) return "";
        String s = appPath.trim();
        return s.isEmpty() ? "" : (s.startsWith("/") ? s : "/" + s);
    }

    /** 判断工作流 JSON 中是否包含指定 appPath 的节点（发布后涉及该 APP 的任务）。 */
    private boolean workflowContainsAppPath(String workflowJson, String appPath) {
        if (workflowJson == null || workflowJson.isBlank() || appPath == null || appPath.isBlank()) {
            return false;
        }
        final String expected = normalizeAppPath(appPath);
        try {
            JsonNode root = OBJECT_MAPPER.readTree(workflowJson);
            JsonNode nodes = root != null ? root.get("nodes") : null;
            if (nodes == null || !nodes.isArray()) return false;
            for (JsonNode node : nodes) {
                JsonNode p = node.get("appPath");
                if (p != null && p.isTextual()) {
                    String nodePath = normalizeAppPath(p.asText());
                    if (expected.equals(nodePath)) return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.debug("parse workflow json for appPath check: {}", e.getMessage());
            return false;
        }
    }

    @GetMapping("/tasks/{id}")
    public ApiResponse<Map<String, Object>> get(@PathVariable Long id) {
        return repository.findById(id)
                .map(this::toMap)
                .map(ApiResponse::ok)
                .orElse(ApiResponse.fail("未找到"));
    }

    /**
     * 创建总任务：保存任务并自动创建同名 AgentApp，写入非空 workflowJson，保证工作台可见、Workflow 有节点可展示。
     */
    @PostMapping("/tasks")
    public ApiResponse<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
        String name = (String) body.get("name");
        if (name == null || name.isBlank()) {
            return ApiResponse.fail("任务名称不能为空");
        }

        String partTypeOrSystem = (String) body.get("partType");
        if (partTypeOrSystem == null || partTypeOrSystem.isBlank()) {
            partTypeOrSystem = (String) body.get("system");
        }

        String templateKey = TaskWorkflowTemplateFactory.resolveTemplateKey(partTypeOrSystem);
        String workflowJson = TaskWorkflowTemplateFactory.getWorkflowJson(partTypeOrSystem);
        if (workflowJson == null || workflowJson.isBlank()) {
            workflowJson = TaskWorkflowTemplateFactory.getDefaultFallbackJson();
            log.warn("workflowJson was empty, using default fallback for task name={}", name);
        }
        log.info("create task: name={}, templateKey={}, workflowJson length={}", name, templateKey, workflowJson.length());

        Instant now = Instant.now();
        AgentApp app = new AgentApp();
        app.setName(name);
        app.setAppType("workflow");
        app.setStatus("草稿");
        app.setWorkflowJson(workflowJson);
        app.setCreateTime(now);
        app.setUpdateTime(now);
        app = agentAppRepository.save(app);
        log.info("created AgentApp: id={}, name={}, workflowJson non-empty={}", app.getId(), app.getName(), app.getWorkflowJson() != null && !app.getWorkflowJson().isEmpty());

        IntegrationTask e = new IntegrationTask();
        e.setName(name);
        e.setSystem((String) body.get("system"));
        // 新建任务未发布工作流前为「待配置」，发布后由前端更新为「排队中」
        e.setStatus(body.get("status") != null ? body.get("status").toString() : "待配置");
        e.setProgress(body.get("progress") != null ? ((Number) body.get("progress")).intValue() : 0);
        e.setCreateTime(now);
        if (body.get("startTime") != null && !body.get("startTime").toString().isBlank())
            e.setStartTime(Instant.parse(body.get("startTime").toString()));
        if (body.get("endTime") != null && !body.get("endTime").toString().isBlank())
            e.setEndTime(Instant.parse(body.get("endTime").toString()));
        if (body.get("runTimeSeconds") != null)
            e.setRunTimeSeconds(((Number) body.get("runTimeSeconds")).longValue());

        e.setAppId(app.getId());
        e.setAppName(app.getName());
        e.setTemplateKey(templateKey);
        e.setWorkflowJsonSnapshot(workflowJson);

        e = repository.save(e);
        Map<String, Object> response = toMap(e);
        response.put("appId", app.getId());
        response.put("appName", app.getName() != null ? app.getName() : "");
        response.put("taskId", e.getId());
        response.put("taskName", e.getName() != null ? e.getName() : "");
        return ApiResponse.ok(response);
    }

    @PutMapping("/tasks/{id}")
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        IntegrationTask e = repository.findById(id).orElse(null);
        if (e == null) return ApiResponse.fail("未找到");
        if (body.containsKey("name")) e.setName((String) body.get("name"));
        if (body.containsKey("system")) e.setSystem((String) body.get("system"));
        if (body.containsKey("status")) e.setStatus(body.get("status").toString());
        if (body.containsKey("progress")) e.setProgress(((Number) body.get("progress")).intValue());
        if (body.containsKey("startTime")) {
            Object v = body.get("startTime");
            e.setStartTime(v != null && !v.toString().isBlank() ? Instant.parse(v.toString()) : null);
        }
        if (body.containsKey("endTime")) {
            Object v = body.get("endTime");
            e.setEndTime(v != null && !v.toString().isBlank() ? Instant.parse(v.toString()) : null);
        }
        if (body.containsKey("runTimeSeconds")) e.setRunTimeSeconds(body.get("runTimeSeconds") != null ? ((Number) body.get("runTimeSeconds")).longValue() : null);
        e = repository.save(e);
        return ApiResponse.ok(toMap(e));
    }

    @DeleteMapping("/tasks/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) return ApiResponse.fail("未找到");
        repository.deleteById(id);
        return ApiResponse.ok(null);
    }

    private Map<String, Object> toMap(IntegrationTask e) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", String.valueOf(e.getId()));
        m.put("name", nullToEmpty(e.getName()));
        m.put("system", nullToEmpty(e.getSystem()));
        m.put("status", nullToEmpty(e.getStatus()));
        m.put("progress", e.getProgress() != null ? e.getProgress() : 0);
        m.put("createTime", TimeFormat.format(e.getCreateTime()));
        m.put("startTime", e.getStartTime() != null ? TimeFormat.format(e.getStartTime()) : "");
        m.put("endTime", e.getEndTime() != null ? TimeFormat.format(e.getEndTime()) : "");
        m.put("runTimeSeconds", e.getRunTimeSeconds() != null ? e.getRunTimeSeconds() : 0L);
        m.put("appId", e.getAppId());
        m.put("appName", nullToEmpty(e.getAppName()));
        m.put("templateKey", nullToEmpty(e.getTemplateKey()));
        m.put("workflowJsonSnapshot", e.getWorkflowJsonSnapshot() != null ? e.getWorkflowJsonSnapshot() : "");
        return m;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
