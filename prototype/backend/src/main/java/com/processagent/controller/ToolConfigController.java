package com.processagent.controller;

import com.processagent.entity.ToolConfig;
import com.processagent.repository.ToolConfigRepository;
import com.processagent.util.ApiResponse;
import com.processagent.util.TimeFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tools")
@RequiredArgsConstructor
public class ToolConfigController {

    private final ToolConfigRepository repository;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        List<ToolConfig> list = repository.findAll();
        if (type != null && !type.isBlank()) {
            list = list.stream().filter(t -> type.equals(t.getType())).toList();
        }
        if (status != null && !status.isBlank()) {
            list = list.stream().filter(t -> status.equals(t.getStatus())).toList();
        }
        if (keyword != null && !keyword.isBlank()) {
            String k = keyword.toLowerCase();
            list = list.stream().filter(t ->
                    (t.getName() != null && t.getName().toLowerCase().contains(k))
                            || (t.getId() != null && String.valueOf(t.getId()).contains(k))
                            || (t.getEndpoint() != null && t.getEndpoint().toLowerCase().contains(k))
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
        ToolConfig e = new ToolConfig();
        e.setName((String) body.get("name"));
        e.setType(body.get("type") != null ? body.get("type").toString() : "mcp");
        e.setEndpoint(body.get("endpoint") != null ? body.get("endpoint").toString() : "");
        e.setStatus(body.get("status") != null ? body.get("status").toString() : "已启用");
        e.setDescription(body.get("desc") != null ? body.get("desc").toString() : (String) body.get("description"));
        e.setUpdateTime(Instant.now());
        e = repository.save(e);
        return ApiResponse.ok(toMap(e));
    }

    @PutMapping("/{id}")
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        ToolConfig e = repository.findById(id).orElse(null);
        if (e == null) return ApiResponse.fail("未找到");
        if (body.containsKey("name")) e.setName((String) body.get("name"));
        if (body.containsKey("type")) e.setType(body.get("type") != null ? body.get("type").toString() : e.getType());
        if (body.containsKey("endpoint")) e.setEndpoint(body.get("endpoint") != null ? body.get("endpoint").toString() : e.getEndpoint());
        if (body.containsKey("status")) e.setStatus(body.get("status") != null ? body.get("status").toString() : e.getStatus());
        if (body.containsKey("desc")) e.setDescription(body.get("desc") != null ? body.get("desc").toString() : e.getDescription());
        if (body.containsKey("description")) e.setDescription(body.get("description") != null ? body.get("description").toString() : e.getDescription());
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

    private Map<String, Object> toMap(ToolConfig e) {
        return Map.of(
                "id", String.valueOf(e.getId()),
                "name", nullToEmpty(e.getName()),
                "type", nullToEmpty(e.getType()),
                "endpoint", nullToEmpty(e.getEndpoint()),
                "status", nullToEmpty(e.getStatus()),
                "desc", nullToEmpty(e.getDescription()),
                "updateTime", TimeFormat.format(e.getUpdateTime())
        );
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}

