package com.processagent.config;

import com.processagent.entity.*;
import com.processagent.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final KnowledgeItemRepository knowledgeItemRepository;
    private final ModelConfigRepository modelConfigRepository;
    private final ProcessPathItemRepository processPathItemRepository;
    private final IntegrationTaskRepository integrationTaskRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OrgRepository orgRepository;
    private final ToolConfigRepository toolConfigRepository;
    private final RuleConfigRepository ruleConfigRepository;
    private final AgentAppRepository agentAppRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (knowledgeItemRepository.count() == 0) {
            log.info("初始化基础数据...");
        List<RoleEntity> roles = List.of(
                createRole("系统管理员", "admin", "全部功能权限", 99),
                createRole("工艺工程师", "process_engineer", "知识库、CAPP/CAM/CAE、工艺路径", 45),
                createRole("查看者", "viewer", "仅查看工艺知识库与工艺路径", 12)
        );
        roleRepository.saveAll(roles);

        List<UserEntity> users = List.of(
                createUser("admin", roles.get(0).getId(), "信息化部", "正常", "2025-03-05 08:30"),
                createUser("zhangsan", roles.get(1).getId(), "工艺部", "正常", "2025-03-05 09:15"),
                createUser("lisi", roles.get(1).getId(), "工艺部", "正常", "2025-03-04 17:20"),
                createUser("wangwu", roles.get(2).getId(), "生产部", "正常", "2025-03-03 14:00")
        );
        userRepository.saveAll(users);

        List<KnowledgeItem> knowledge = List.of(
                createKnowledge("车削工艺规范", "切削工艺", "工艺规范", "v2.1", "2025-03-01", "已发布"),
                createKnowledge("铣削参数库", "切削工艺", "参数库", "v1.5", "2025-02-28", "已发布"),
                createKnowledge("装配工艺卡片模板", "装配工艺", "模板", "v3.0", "2025-03-02", "已发布"),
                createKnowledge("热处理工艺知识", "热处理", "工艺规范", "v1.2", "2025-02-20", "草稿"),
                createKnowledge("钣金折弯系数表", "钣金工艺", "参数库", "v2.0", "2025-03-03", "已发布")
        );
        knowledgeItemRepository.saveAll(knowledge);

        List<ModelConfig> models = List.of(
                createModel("GPT-4", "llm", "OpenAI", "https://api.openai.com/v1", "可用", "2025-03-05 10:00"),
                createModel("通义千问-Max", "llm", "阿里云", "dashscope.aliyun.com", "可用", "2025-03-04 15:20"),
                createModel("text-embedding-3", "embedding", "OpenAI", "https://api.openai.com/v1/embeddings", "可用", "2025-03-03 09:00"),
                createModel("Qwen-1.8B-工艺助手", "small", "本地", "http://localhost:8000/v1", "可用", "2025-03-05 08:30")
        );
        modelConfigRepository.saveAll(models);

        List<ProcessPathItem> paths = List.of(
                createPath("轴类零件-001", "下料 → 粗车 → 热处理 → 精车 → 磨削 → 检验", 6, "已输出", "2025-03-05 10:15"),
                createPath("壳体-002", "铸造 → 时效 → 铣面 → 钻孔 → 攻丝 → 表面处理", 6, "已输出", "2025-03-05 09:45"),
                createPath("齿轮-003", "下料 → 锻造 → 正火 → 车削 → 滚齿 → 热处理 → 磨齿", 7, "生成中", null)
        );
        processPathItemRepository.saveAll(paths);

        List<IntegrationTask> tasks = List.of(
                createTask("零件工艺规程编制", "CAPP", "已完成", 100, "2025-03-05 09:00"),
                createTask("NC 刀路生成", "CAM", "进行中", 65, "2025-03-05 10:30"),
                createTask("结构仿真分析", "CAE", "已完成", 100, "2025-03-04 14:20"),
                createTask("工艺卡片导出", "CAPP", "排队中", 0, "2025-03-05 11:00")
        );
        integrationTaskRepository.saveAll(tasks);
        }

        if (roleRepository.count() == 0) {
            log.info("初始化角色数据...");
            List<RoleEntity> roles = List.of(
                    createRole("系统管理员", "admin", "全部功能权限", 99),
                    createRole("工艺工程师", "process_engineer", "知识库、CAPP/CAM/CAE、工艺路径", 45),
                    createRole("查看者", "viewer", "仅查看工艺知识库与工艺路径", 12)
            );
            roleRepository.saveAll(roles);
            log.info("角色初始化完成");
        }
        if (userRepository.count() == 0) {
            log.info("初始化用户数据...");
            Long adminRoleId = roleRepository.findByCode("admin").map(RoleEntity::getId).orElse(null);
            Long processRoleId = roleRepository.findByCode("process_engineer").map(RoleEntity::getId).orElse(null);
            Long viewerRoleId = roleRepository.findByCode("viewer").map(RoleEntity::getId).orElse(null);
            List<UserEntity> users = List.of(
                    createUser("admin", adminRoleId, "信息化部", "正常", "2025-03-05 08:30"),
                    createUser("zhangsan", processRoleId, "工艺部", "正常", "2025-03-05 09:15"),
                    createUser("lisi", processRoleId, "工艺部", "正常", "2025-03-04 17:20"),
                    createUser("wangwu", viewerRoleId, "生产部", "正常", "2025-03-03 14:00")
            );
            userRepository.saveAll(users);
            log.info("用户初始化完成");
        }
        if (orgRepository.count() == 0) {
            log.info("初始化组织机构数据...");
            OrgEntity root = new OrgEntity();
            root.setName("公司");
            root.setShortName("公司");
            root.setCode("ROOT");
            root.setOrgType("公司");
            root.setParentId(null);
            root.setHeadCount(0);
            root.setSortOrder(0);
            root = orgRepository.save(root);
            List<OrgEntity> depts = List.of(
                    createOrg("信息化部", "信息部", "IT", "部门", root.getId(), 0, 1),
                    createOrg("工艺部", "工艺部", "PROCESS", "部门", root.getId(), 0, 2),
                    createOrg("生产部", "生产部", "PROD", "部门", root.getId(), 0, 3),
                    createOrg("质量部", "质量部", "QA", "部门", root.getId(), 0, 4)
            );
            orgRepository.saveAll(depts);
            log.info("组织机构初始化完成");
        }
        if (toolConfigRepository.count() == 0) {
            List<ToolConfig> tools = List.of(
                    createTool("文件系统 MCP", "mcp", "mcp://localhost:3000/files", "已启用", "读写本地文件、目录列表"),
                    createTool("数据库 MCP", "mcp", "mcp://localhost:3001/db", "已启用", "查询与写入数据库"),
                    createTool("工艺知识库 API", "api", "/api/knowledge/search", "已启用", "检索工艺知识库"),
                    createTool("自定义脚本", "custom", "-", "已停用", "用户自定义脚本")
            );
            toolConfigRepository.saveAll(tools);
        }
        if (ruleConfigRepository.count() == 0) {
            List<RuleConfig> rules = List.of(
                    createRule("示例规则-复材成型", "当零件类型为「复材成型」时，执行铺放-固化联合仿真并输出变形预测。", "# 示例逻辑输出\\nsteps: [铺放, 固化, 联合仿真, 代理模型预测]", "草稿")
            );
            ruleConfigRepository.saveAll(rules);
        }
        if (agentAppRepository.count() == 0) {
            AgentApp app = new AgentApp();
            app.setName("工艺工作流");
            app.setDescription("工序编排");
            app.setAppType("chatflow");
            app.setStatus("已发布");
            app.setPublicUrl("/public/apps/1");
            app.setCreateTime(Instant.now());
            app.setUpdateTime(Instant.now());
            app.setPublishTime(Instant.now());
            app.setWorkflowJson("");
            agentAppRepository.save(app);
        }
        log.info("基础数据初始化完成");
    }


    private static OrgEntity createOrg(String name, String shortName, String code, String orgType, Long parentId, int headCount, int sortOrder) {
        OrgEntity o = new OrgEntity();
        o.setName(name);
        o.setShortName(shortName);
        o.setCode(code);
        o.setOrgType(orgType);
        o.setParentId(parentId);
        o.setHeadCount(headCount);
        o.setSortOrder(sortOrder);
        return o;
    }

    private static RoleEntity createRole(String name, String code, String desc, int permCount) {
        RoleEntity r = new RoleEntity();
        r.setName(name);
        r.setCode(code);
        r.setDescription(desc);
        r.setPermissionCount(permCount);
        return r;
    }

    private static UserEntity createUser(String username, Long roleId, String dept, String status, String lastLoginStr) {
        UserEntity u = new UserEntity();
        u.setUsername(username);
        u.setRealName(username);
        u.setRoleId(roleId);
        u.setDept(dept);
        u.setPersonnelCategory("正式在编");
        u.setPositionType("技术");
        u.setJobTitle("");
        u.setContact("");
        u.setStatus(status);
        u.setLastLogin(parseInstant(lastLoginStr));
        return u;
    }

    private static KnowledgeItem createKnowledge(String name, String category, String type, String version, String updateTime, String status) {
        KnowledgeItem k = new KnowledgeItem();
        k.setName(name);
        k.setCategory(category);
        k.setType(type);
        k.setVersion(version);
        k.setUpdateTime(parseInstant(updateTime + " 00:00"));
        k.setStatus(status);
        return k;
    }

    private static ModelConfig createModel(String name, String type, String provider, String endpoint, String status, String updateTime) {
        ModelConfig m = new ModelConfig();
        m.setName(name);
        m.setType(type);
        m.setProvider(provider);
        m.setEndpoint(endpoint);
        m.setStatus(status);
        m.setUpdateTime(parseInstant(updateTime));
        return m;
    }

    private static ProcessPathItem createPath(String partName, String path, int steps, String status, String outputTime) {
        ProcessPathItem p = new ProcessPathItem();
        p.setPartName(partName);
        p.setPath(path);
        p.setSteps(steps);
        p.setStatus(status);
        p.setOutputTime(outputTime != null ? parseInstant(outputTime) : null);
        return p;
    }

    private static IntegrationTask createTask(String name, String system, String status, int progress, String createTime) {
        IntegrationTask t = new IntegrationTask();
        t.setName(name);
        t.setSystem(system);
        t.setStatus(status);
        t.setProgress(progress);
        t.setCreateTime(parseInstant(createTime));
        return t;
    }

    private static ToolConfig createTool(String name, String type, String endpoint, String status, String desc) {
        ToolConfig t = new ToolConfig();
        t.setName(name);
        t.setType(type);
        t.setEndpoint(endpoint);
        t.setStatus(status);
        t.setDescription(desc);
        t.setUpdateTime(Instant.now());
        return t;
    }

    private static RuleConfig createRule(String name, String nl, String logic, String status) {
        RuleConfig r = new RuleConfig();
        r.setName(name);
        r.setNaturalLanguage(nl);
        r.setLogicOutput(logic);
        r.setStatus(status);
        r.setUpdateTime(Instant.now());
        return r;
    }

    private static Instant parseInstant(String s) {
        if (s == null || s.isEmpty()) return Instant.now();
        try {
            // 按本地时间  yyyy-MM-dd HH:mm 解析后转 UTC（按 +8 处理）
            String normalized = s.trim().replace(" ", "T");
            if (normalized.length() == 16) normalized += ":00";
            return java.time.LocalDateTime.parse(normalized)
                    .atZone(java.time.ZoneId.of("Asia/Shanghai"))
                    .toInstant();
        } catch (Exception e) {
            return Instant.now();
        }
    }
}
