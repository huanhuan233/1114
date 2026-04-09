package com.processagent.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 与前端 Workflow.vue 的 nodePickerItems / appPickerItems 对齐的节点注册表。
 * 用于任务自动生成工作流时拼装真实业务节点，不脱离现有节点体系。
 */
public final class TaskNodeRegistry {

    private TaskNodeRegistry() {
    }

    /**
     * 节点元数据，与前端可识别的字段一致。
     */
    public static class NodeDef {
        private final String key;
        private final String label;
        private final String nodeType;
        private final String group;
        private final String appPath;
        private final String description;
        private final List<String> inputs;
        private final List<String> outputs;
        private final List<Map<String, Object>> configSchema;
        private final String mockResultTemplate;

        public NodeDef(String key, String label, String nodeType, String group,
                       String appPath, String description, List<String> inputs, List<String> outputs,
                       List<Map<String, Object>> configSchema, String mockResultTemplate) {
            this.key = key;
            this.label = label;
            this.nodeType = nodeType;
            this.group = group;
            this.appPath = appPath != null ? appPath : "";
            this.description = description != null ? description : "";
            this.inputs = inputs != null ? inputs : List.of();
            this.outputs = outputs != null ? outputs : List.of();
            this.configSchema = configSchema != null ? configSchema : List.of();
            this.mockResultTemplate = mockResultTemplate != null ? mockResultTemplate : "该节点已完成模拟运行。";
        }

        public String getKey() { return key; }
        public String getLabel() { return label; }
        public String getNodeType() { return nodeType; }
        public String getGroup() { return group; }
        public String getAppPath() { return appPath; }
        public String getDescription() { return description; }
        public List<String> getInputs() { return inputs; }
        public List<String> getOutputs() { return outputs; }
        public List<Map<String, Object>> getConfigSchema() { return configSchema; }
        public String getMockResultTemplate() { return mockResultTemplate; }
    }

    // ---------- 通用节点（与 Workflow.vue nodePickerItems 对应） ----------
    public static final String KEY_START = "start";
    public static final String KEY_END = "end";
    public static final String KEY_AGENT = "agent";
    public static final String KEY_LLM = "llm";
    public static final String KEY_BRANCH = "branch";
    public static final String KEY_HUMAN = "human";
    public static final String KEY_DIRECT = "direct";
    public static final String KEY_TEMPLATE = "template";
    public static final String KEY_AGGREGATE = "aggregate";
    public static final String KEY_REPORT = "report";

    // ---------- APP 节点 key（与 Workflow.vue appPickerItems 一致） ----------
    public static final String KEY_APP_STRUCTURE = "app-structure";
    public static final String KEY_APP_FEASIBILITY = "app-feasibility";
    public static final String KEY_APP_SPECIAL = "app-special";
    public static final String KEY_APP_LAYING = "app-laying";
    public static final String KEY_APP_FILAMENT = "app-filament";
    public static final String KEY_APP_CONDUIT = "app-conduit";
    public static final String KEY_APP_SUPERPLASTIC = "app-superplastic";
    public static final String KEY_APP_CAE_PROCESS = "app-cae-process";
    public static final String KEY_APP_CAE_SOLVER = "app-cae-solver";

    private static final java.util.Map<String, NodeDef> REGISTRY = Map.ofEntries(
            // 系统：开始 / 结束
            entry(KEY_START, "开始", "start", "系统", "", "工作流起始节点", List.of(), List.of(), List.of(), "开始节点无需执行。"),
            entry(KEY_END, "结束", "end", "系统", "", "工作流结束节点", List.of("上游输出"), List.of(), List.of(), "工作流已结束。"),
            // 通用
            entry(KEY_AGENT, "工艺推理", "agent", "基础", "", "工艺推理智能体节点", List.of("零件信息", "工艺参数"), List.of("推理结果", "建议"), schema("mode", "运行模式", "select", List.of("快速", "详细")), "节点【工艺推理】模拟运行完成，已输出示例结果。"),
            entry(KEY_LLM, "LLM", "llm", "基础", "", "大模型对话与推理节点", List.of("输入"), List.of("输出"), List.of(), "节点【LLM】模拟运行完成。"),
            entry(KEY_BRANCH, "条件分支", "branch", "逻辑", "", "IF/ELSE 条件分支", List.of(), List.of(), List.of(), "分支节点已评估。"),
            entry(KEY_HUMAN, "人工介入", "human", "逻辑", "", "人工审核或介入节点", List.of(), List.of(), List.of(), "人工节点已处理。"),
            entry(KEY_DIRECT, "直接回复", "direct", "基础", "", "不经推理直接输出", List.of(), List.of(), List.of(), "直接回复已输出。"),
            entry(KEY_TEMPLATE, "模板转换", "template", "转换", "", "模板转换节点", List.of("输入"), List.of("输出"), List.of(), "节点【模板转换】模拟运行完成。"),
            entry(KEY_AGGREGATE, "变量聚合器", "aggregate", "转换", "", "变量聚合", List.of("多路输入"), List.of("聚合结果"), List.of(), "节点【变量聚合器】模拟运行完成。"),
            entry(KEY_REPORT, "报告生成", "report", "输出", "", "报告生成节点", List.of("数据"), List.of("报告"), List.of(), "节点【报告生成】模拟运行完成。"),
            // 可制造性分析与优化软件
            entry(KEY_APP_STRUCTURE, "结构合理性分析APP", "app", "可制造性分析与优化软件", "/integration/process-review/structure", "结构合理性分析", List.of("零件信息", "结构参数"), List.of("分析结果", "优化建议"), schema("mode", "运行模式", "select", List.of("快速", "详细")), "节点【结构合理性分析APP】模拟运行完成，已输出示例结果。"),
            entry(KEY_APP_FEASIBILITY, "工艺方案可行性评估和优化APP", "app", "可制造性分析与优化软件", "/integration/process-review/feasibility", "工艺方案可行性评估和优化分析", List.of("零件信息", "工艺参数"), List.of("评估结果", "优化建议"), schema("mode", "运行模式", "select", List.of("快速", "详细")), "节点【工艺方案可行性评估和优化APP】模拟运行完成，已输出示例结果。"),
            // 特种工艺决策与指令生成软件
            entry(KEY_APP_SPECIAL, "特种工艺决策与指令生成APP", "app", "特种工艺决策与指令生成软件", "/integration/capp/special", "特种工艺决策与指令生成", List.of("零件信息", "工艺要求"), List.of("工艺指令", "决策结果"), List.of(), "节点【特种工艺决策与指令生成APP】模拟运行完成，已输出示例结果。"),
            entry(KEY_APP_LAYING, "复材铺放工艺决策APP", "app", "特种工艺决策与指令生成软件", "/integration/capp/laying", "复材铺放工艺决策", List.of("铺层信息", "工艺参数"), List.of("铺放指令", "仿真结果"), List.of(), "节点【复材铺放工艺决策APP】模拟运行完成，已输出示例结果。"),
            entry(KEY_APP_FILAMENT, "复材铺丝工艺编程APP", "app", "特种工艺决策与指令生成软件", "/integration/capp/filament", "复材铺丝工艺编程", List.of("零件模型", "工艺参数"), List.of("编程结果", "NC代码"), List.of(), "节点【复材铺丝工艺编程APP】模拟运行完成，已输出示例结果。"),
            entry(KEY_APP_CONDUIT, "导管成形工艺仿真APP", "app", "特种工艺决策与指令生成软件", "/integration/capp/conduit", "导管成形工艺仿真", List.of("零件信息", "工艺参数"), List.of("仿真结果", "成形建议"), List.of(), "节点【导管成形工艺仿真APP】模拟运行完成，已输出示例结果。"),
            entry(KEY_APP_SUPERPLASTIC, "超塑成形工艺仿真APP", "app", "特种工艺决策与指令生成软件", "/integration/capp/superplastic", "超塑成形工艺仿真", List.of("零件信息", "工艺参数"), List.of("仿真结果", "优化建议"), List.of(), "节点【超塑成形工艺仿真APP】模拟运行完成，已输出示例结果。"),
            // 复材成型工艺高精度仿真软件
            entry(KEY_APP_CAE_PROCESS, "复材制造工艺过程仿真APP", "app", "复材成型工艺高精度仿真软件", "/integration/cae/process", "复材制造工艺过程仿真", List.of("工艺参数", "材料参数"), List.of("仿真结果", "过程数据"), List.of(), "节点【复材制造工艺过程仿真APP】模拟运行完成，已输出示例结果。"),
            entry(KEY_APP_CAE_SOLVER, "渗流求解器与多物理耦合器APP", "app", "复材成型工艺高精度仿真软件", "/integration/cae/solver", "渗流求解与多物理耦合", List.of("边界条件", "材料参数"), List.of("求解结果", "场分布"), List.of(), "节点【渗流求解器与多物理耦合器APP】模拟运行完成，已输出示例结果。")
    );

    private static Map.Entry<String, NodeDef> entry(String key, String label, String nodeType, String group,
                                                    String appPath, String description, List<String> inputs, List<String> outputs,
                                                    List<Map<String, Object>> configSchema, String mockResultTemplate) {
        return Map.entry(key, new NodeDef(key, label, nodeType, group, appPath, description, inputs, outputs, configSchema, mockResultTemplate));
    }

    private static List<Map<String, Object>> schema(String field, String label, String type, List<String> options) {
        Map<String, Object> m = new java.util.LinkedHashMap<>();
        m.put("field", field);
        m.put("label", label);
        m.put("type", type);
        if (options != null) m.put("options", options);
        return List.of(m);
    }

    public static Optional<NodeDef> get(String key) {
        return Optional.ofNullable(REGISTRY.get(key));
    }

    public static NodeDef getOrThrow(String key) {
        NodeDef d = REGISTRY.get(key);
        if (d == null) throw new IllegalArgumentException("unknown node key: " + key);
        return d;
    }
}
