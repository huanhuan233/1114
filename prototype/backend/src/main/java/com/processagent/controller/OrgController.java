package com.processagent.controller;

import com.processagent.entity.OrgEntity;
import com.processagent.repository.OrgRepository;
import com.processagent.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/org")
@RequiredArgsConstructor
public class OrgController {

    private final OrgRepository repository;

    @GetMapping("/tree")
    public ApiResponse<List<Map<String, Object>>> tree() {
        List<OrgEntity> all = repository.findAll();
        List<Map<String, Object>> roots = buildChildren(all, null);
        return ApiResponse.ok(roots);
    }

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list() {
        List<OrgEntity> all = repository.findAll();
        return ApiResponse.ok(all.stream().map(this::toMap).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> get(@PathVariable Long id) {
        return repository.findById(id)
                .map(this::toMap)
                .map(ApiResponse::ok)
                .orElse(ApiResponse.fail("未找到"));
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
        OrgEntity e = new OrgEntity();
        e.setName((String) body.get("name"));
        e.setShortName((String) body.get("shortName"));
        e.setCode((String) body.get("code"));
        e.setOrgType((String) body.get("orgType"));
        e.setParentId(body.get("parentId") != null ? ((Number) body.get("parentId")).longValue() : null);
        e.setHeadCount(body.get("headCount") != null ? ((Number) body.get("headCount")).intValue() : 0);
        e.setSortOrder(body.get("sortOrder") != null ? ((Number) body.get("sortOrder")).intValue() : 0);
        e = repository.save(e);
        return ApiResponse.ok(toMap(e));
    }

    @PutMapping("/{id}")
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        OrgEntity e = repository.findById(id).orElse(null);
        if (e == null) return ApiResponse.fail("未找到");
        if (body.containsKey("name")) e.setName((String) body.get("name"));
        if (body.containsKey("shortName")) e.setShortName((String) body.get("shortName"));
        if (body.containsKey("code")) e.setCode((String) body.get("code"));
        if (body.containsKey("orgType")) e.setOrgType((String) body.get("orgType"));
        if (body.containsKey("parentId")) e.setParentId(body.get("parentId") != null ? ((Number) body.get("parentId")).longValue() : null);
        if (body.containsKey("headCount")) e.setHeadCount(body.get("headCount") != null ? ((Number) body.get("headCount")).intValue() : 0);
        if (body.containsKey("sortOrder")) e.setSortOrder(((Number) body.get("sortOrder")).intValue());
        e = repository.save(e);
        return ApiResponse.ok(toMap(e));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) return ApiResponse.fail("未找到");
        long childCount = repository.findByParentIdOrderBySortOrderAsc(id).size();
        if (childCount > 0) return ApiResponse.fail("存在子部门，无法删除");
        repository.deleteById(id);
        return ApiResponse.ok(null);
    }

    private List<Map<String, Object>> buildChildren(List<OrgEntity> all, Long parentId) {
        return all.stream()
                .filter(o -> (parentId == null && o.getParentId() == null) || (parentId != null && parentId.equals(o.getParentId())))
                .sorted((a, b) -> Integer.compare(a.getSortOrder() != null ? a.getSortOrder() : 0, b.getSortOrder() != null ? b.getSortOrder() : 0))
                .map(o -> {
                    List<Map<String, Object>> children = buildChildren(all, o.getId());
                    Map<String, Object> m = toMap(o);
                    Map<String, Object> withChildren = new HashMap<>(m);
                    withChildren.put("children", children != null ? children : List.of());
                    return withChildren;
                })
                .collect(Collectors.toList());
    }

    private Map<String, Object> toMap(OrgEntity e) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", String.valueOf(e.getId()));
        m.put("name", nullToEmpty(e.getName()));
        m.put("shortName", nullToEmpty(e.getShortName()));
        m.put("code", nullToEmpty(e.getCode()));
        m.put("orgType", nullToEmpty(e.getOrgType()));
        m.put("parentId", e.getParentId() != null ? String.valueOf(e.getParentId()) : "");
        m.put("headCount", e.getHeadCount() != null ? e.getHeadCount() : 0);
        m.put("sortOrder", e.getSortOrder() != null ? e.getSortOrder() : 0);
        return m;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
