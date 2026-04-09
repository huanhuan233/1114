package com.processagent.controller;

import com.processagent.entity.KnowledgeItem;
import com.processagent.repository.KnowledgeItemRepository;
import com.processagent.util.ApiResponse;
import com.processagent.util.TimeFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {

    private final KnowledgeItemRepository repository;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {
        List<KnowledgeItem> list = repository.findAll();
        if (category != null && !category.isBlank()) {
            list = list.stream().filter(k -> category.equals(k.getCategory())).toList();
        }
        if (type != null && !type.isBlank()) {
            list = list.stream().filter(k -> type.equals(k.getType())).toList();
        }
        if (keyword != null && !keyword.isBlank()) {
            String k = keyword.toLowerCase();
            list = list.stream().filter(item ->
                    (item.getName() != null && item.getName().toLowerCase().contains(k))
                            || (item.getId() != null && String.valueOf(item.getId()).contains(k))
            ).toList();
        }
        List<Map<String, Object>> data = list.stream().map(this::toMap).collect(Collectors.toList());
        return ApiResponse.ok(data);
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
        KnowledgeItem e = new KnowledgeItem();
        e.setName((String) body.get("name"));
        e.setCategory((String) body.get("category"));
        e.setType((String) body.get("type"));
        e.setVersion(body.get("version") != null ? body.get("version").toString() : "v1.0");
        e.setUpdateTime(Instant.now());
        e.setStatus(body.get("status") != null ? body.get("status").toString() : "草稿");
        e = repository.save(e);
        return ApiResponse.ok(toMap(e));
    }

    @PutMapping("/{id}")
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        KnowledgeItem e = repository.findById(id).orElse(null);
        if (e == null) return ApiResponse.fail("未找到");
        if (body.containsKey("name")) e.setName((String) body.get("name"));
        if (body.containsKey("category")) e.setCategory((String) body.get("category"));
        if (body.containsKey("type")) e.setType((String) body.get("type"));
        if (body.containsKey("version")) e.setVersion(body.get("version").toString());
        if (body.containsKey("status")) e.setStatus(body.get("status").toString());
        e.setUpdateTime(Instant.now());
        e = repository.save(e);
        return ApiResponse.ok(toMap(e));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) return ApiResponse.fail("未找到");
        repository.deleteById(id);
        return ApiResponse.ok(null);
    }

    private Map<String, Object> toMap(KnowledgeItem e) {
        return Map.of(
                "id", String.valueOf(e.getId()),
                "name", nullToEmpty(e.getName()),
                "category", nullToEmpty(e.getCategory()),
                "type", nullToEmpty(e.getType()),
                "version", nullToEmpty(e.getVersion()),
                "updateTime", TimeFormat.formatDate(e.getUpdateTime()),
                "status", nullToEmpty(e.getStatus())
        );
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
