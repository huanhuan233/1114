package com.processagent.controller;

import com.processagent.entity.ModelConfig;
import com.processagent.repository.ModelConfigRepository;
import com.processagent.util.ApiResponse;
import com.processagent.util.TimeFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/models")
@RequiredArgsConstructor
public class ModelConfigController {

    private final ModelConfigRepository repository;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String keyword) {
        List<ModelConfig> list = repository.findAll();
        if (type != null && !type.isBlank()) {
            list = list.stream().filter(m -> type.equals(m.getType())).toList();
        }
        if (keyword != null && !keyword.isBlank()) {
            String k = keyword.toLowerCase();
            list = list.stream().filter(m ->
                    (m.getName() != null && m.getName().toLowerCase().contains(k))
                            || (m.getId() != null && String.valueOf(m.getId()).contains(k))
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
        ModelConfig e = new ModelConfig();
        e.setName((String) body.get("name"));
        e.setType((String) body.get("type"));
        e.setProvider((String) body.get("provider"));
        e.setEndpoint((String) body.get("endpoint"));
        e.setApiKey((String) body.get("apiKey"));
        e.setRemark((String) body.get("remark"));
        e.setStatus(body.get("status") != null ? body.get("status").toString() : "可用");
        e.setUpdateTime(Instant.now());
        e = repository.save(e);
        return ApiResponse.ok(toMap(e));
    }

    @PutMapping("/{id}")
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        ModelConfig e = repository.findById(id).orElse(null);
        if (e == null) return ApiResponse.fail("未找到");
        if (body.containsKey("name")) e.setName((String) body.get("name"));
        if (body.containsKey("type")) e.setType((String) body.get("type"));
        if (body.containsKey("provider")) e.setProvider((String) body.get("provider"));
        if (body.containsKey("endpoint")) e.setEndpoint((String) body.get("endpoint"));
        if (body.containsKey("apiKey")) e.setApiKey((String) body.get("apiKey"));
        if (body.containsKey("remark")) e.setRemark((String) body.get("remark"));
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

    private Map<String, Object> toMap(ModelConfig e) {
        return Map.of(
                "id", String.valueOf(e.getId()),
                "name", nullToEmpty(e.getName()),
                "type", nullToEmpty(e.getType()),
                "provider", nullToEmpty(e.getProvider()),
                "endpoint", nullToEmpty(e.getEndpoint()),
                "status", nullToEmpty(e.getStatus()),
                "updateTime", TimeFormat.format(e.getUpdateTime())
        );
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
