package com.processagent.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;

/**
 * 根据任务零件类型生成工作流 JSON，使用 TaskNodeRegistry 中的真实业务节点，
 * 与前端 Workflow.vue 的节点体系、nodePosition/nodeMeta/配置面板兼容。
 */
public final class TaskWorkflowTemplateFactory {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static final String TEMPLATE_COMPOSITE_FORMING = "composite-forming-template";
    public static final String TEMPLATE_COMPOSITE_TRIMMING = "composite-trimming-template";
    public static final String TEMPLATE_CONDUIT_PROCESS = "conduit-process-template";
    public static final String TEMPLATE_SUPERPLASTIC_FORMING = "superplastic-forming-template";
    public static final String TEMPLATE_DEFAULT = "default-fallback-template";

    private TaskWorkflowTemplateFactory() {
    }

    public static String resolveTemplateKey(String partTypeOrSystem) {
        if (partTypeOrSystem == null || partTypeOrSystem.isBlank()) return TEMPLATE_DEFAULT;
        String s = partTypeOrSystem.trim();
        return switch (s) {
            case "复材成型" -> TEMPLATE_COMPOSITE_FORMING;
            case "复材切边" -> TEMPLATE_COMPOSITE_TRIMMING;
            case "导管成形" -> TEMPLATE_CONDUIT_PROCESS;
            case "超塑成形" -> TEMPLATE_SUPERPLASTIC_FORMING;
            default -> TEMPLATE_DEFAULT;
        };
    }

    /**
     * 根据零件类型生成 workflowJson：命中模板用真实业务节点链，否则用兜底模板。保证非空。
     */
    public static String getWorkflowJson(String partTypeOrSystem) {
        String templateKey = resolveTemplateKey(partTypeOrSystem);
        String taskType = taskTypeFromTemplateKey(templateKey);
        String json = buildTemplateWorkflowJson(templateKey, taskType);
        if (json != null && !json.isBlank()) return json;
        return getDefaultFallbackJson();
    }

    public static String getWorkflowJsonByTemplateKey(String templateKey) {
        String taskType = taskTypeFromTemplateKey(templateKey != null ? templateKey : TEMPLATE_DEFAULT);
        return buildTemplateWorkflowJson(templateKey != null ? templateKey : TEMPLATE_DEFAULT, taskType);
    }

    public static String getDefaultFallbackJson() {
        return buildTemplateWorkflowJson(TEMPLATE_CONDUIT_PROCESS, "导管成形");
    }

    private static String taskTypeFromTemplateKey(String templateKey) {
        if (templateKey == null) return "通用";
        return switch (templateKey) {
            case TEMPLATE_COMPOSITE_FORMING -> "复材成型";
            case TEMPLATE_COMPOSITE_TRIMMING -> "复材切边";
            case TEMPLATE_CONDUIT_PROCESS -> "导管成形";
            case TEMPLATE_SUPERPLASTIC_FORMING -> "超塑成形";
            default -> "通用";
        };
    }

    /**
     * 按模板 key 拼装节点链，使用 TaskNodeRegistry 中的 key。
     * 导管成形: start -> agent -> app-conduit -> app-feasibility -> app-special
     * 复材成型: start -> agent -> app-laying -> app-cae-process -> app-cae-solver
     * 复材切边: start -> agent -> app-filament -> app-feasibility
     * 超塑成形: start -> agent -> app-superplastic -> app-special
     * default: 同导管成形
     */
    private static String buildTemplateWorkflowJson(String templateKey, String taskType) {
        List<String> nodeKeys = nodeKeysForTemplate(templateKey);
        List<Map<String, Object>> nodes = new ArrayList<>();
        List<Map<String, Object>> edges = new ArrayList<>();
        int x = 80;
        int y = 160;
        String prevId = null;
        for (int i = 0; i < nodeKeys.size(); i++) {
            String key = nodeKeys.get(i);
            TaskNodeRegistry.NodeDef def = TaskNodeRegistry.get(key).orElse(null);
            if (def == null) continue;
            String id = "node-" + (i + 1);
            nodes.add(toNodeMap(id, def, x, y));
            if (prevId != null) edges.add(edge("edge-" + i, prevId, id));
            prevId = id;
            x += 200;
        }
        if (nodes.isEmpty()) return null;

        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("source", "task-auto-generated");
        meta.put("templateKey", templateKey);
        meta.put("taskType", taskType);
        meta.put("mock", true);

        Map<String, Object> workflow = new LinkedHashMap<>();
        workflow.put("meta", meta);
        workflow.put("nodes", nodes);
        workflow.put("edges", edges);
        return toJson(workflow);
    }

    private static List<String> nodeKeysForTemplate(String templateKey) {
        if (TEMPLATE_CONDUIT_PROCESS.equals(templateKey) || TEMPLATE_DEFAULT.equals(templateKey)) {
            return List.of(TaskNodeRegistry.KEY_START, TaskNodeRegistry.KEY_AGENT,
                    TaskNodeRegistry.KEY_APP_CONDUIT, TaskNodeRegistry.KEY_APP_FEASIBILITY, TaskNodeRegistry.KEY_APP_SPECIAL);
        }
        if (TEMPLATE_COMPOSITE_FORMING.equals(templateKey)) {
            return List.of(TaskNodeRegistry.KEY_START, TaskNodeRegistry.KEY_AGENT,
                    TaskNodeRegistry.KEY_APP_LAYING, TaskNodeRegistry.KEY_APP_CAE_PROCESS, TaskNodeRegistry.KEY_APP_CAE_SOLVER);
        }
        if (TEMPLATE_COMPOSITE_TRIMMING.equals(templateKey)) {
            return List.of(TaskNodeRegistry.KEY_START, TaskNodeRegistry.KEY_AGENT,
                    TaskNodeRegistry.KEY_APP_FILAMENT, TaskNodeRegistry.KEY_APP_FEASIBILITY);
        }
        if (TEMPLATE_SUPERPLASTIC_FORMING.equals(templateKey)) {
            return List.of(TaskNodeRegistry.KEY_START, TaskNodeRegistry.KEY_AGENT,
                    TaskNodeRegistry.KEY_APP_SUPERPLASTIC, TaskNodeRegistry.KEY_APP_SPECIAL);
        }
        return List.of(TaskNodeRegistry.KEY_START, TaskNodeRegistry.KEY_AGENT,
                TaskNodeRegistry.KEY_APP_CONDUIT, TaskNodeRegistry.KEY_APP_FEASIBILITY, TaskNodeRegistry.KEY_APP_SPECIAL);
    }

    /**
     * 生成与 Workflow.vue WorkflowNode 兼容的节点 Map：含 x/y、appPath、group、inputs、outputs、configSchema、mockRuntime、config。
     */
    private static Map<String, Object> toNodeMap(String id, TaskNodeRegistry.NodeDef def, int x, int y) {
        Map<String, Object> n = new LinkedHashMap<>();
        n.put("id", id);
        n.put("label", def.getLabel());
        n.put("nodeType", def.getNodeType());
        n.put("modelId", "");
        n.put("knowledgeIds", List.of());
        n.put("desc", def.getDescription());
        n.put("x", x);
        n.put("y", y);
        n.put("position", Map.of("x", x, "y", y));
        if (def.getAppPath() != null && !def.getAppPath().isEmpty()) {
            n.put("appPath", def.getAppPath());
        }
        n.put("group", def.getGroup());
        n.put("inputs", def.getInputs());
        n.put("outputs", def.getOutputs());
        n.put("configSchema", def.getConfigSchema());
        n.put("mockRuntime", new LinkedHashMap<String, Object>() {{
            put("status", "idle");
            put("lastRunTime", null);
            put("mockResult", "");
        }});
        n.put("mockResultTemplate", def.getMockResultTemplate());
        n.put("config", new LinkedHashMap<String, Object>());
        if (TaskNodeRegistry.KEY_BRANCH.equals(def.getKey())) {
            n.put("branch", Map.of("ifRules", List.of()));
        }
        return n;
    }

    private static Map<String, Object> edge(String id, String source, String target) {
        Map<String, Object> e = new LinkedHashMap<>();
        e.put("id", id);
        e.put("source", source);
        e.put("target", target);
        return e;
    }

    private static String toJson(Map<String, Object> workflow) {
        try {
            return OBJECT_MAPPER.writeValueAsString(workflow);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("生成工作流 JSON 失败", e);
        }
    }
}
