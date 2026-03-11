package com.processagent.controller;

import com.processagent.entity.RuleConfig;
import com.processagent.repository.RuleConfigRepository;
import com.processagent.util.ApiResponse;
import com.processagent.util.TimeFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
public class RuleConfigController {

    private final RuleConfigRepository repository;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        List<RuleConfig> list = repository.findAll();
        if (status != null && !status.isBlank()) {
            list = list.stream().filter(r -> status.equals(r.getStatus())).toList();
        }
        if (keyword != null && !keyword.isBlank()) {
            String k = keyword.toLowerCase();
            list = list.stream().filter(r ->
                    (r.getName() != null && r.getName().toLowerCase().contains(k))
                            || (r.getId() != null && String.valueOf(r.getId()).contains(k))
                            || (r.getNaturalLanguage() != null && r.getNaturalLanguage().toLowerCase().contains(k))
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
        RuleConfig e = new RuleConfig();
        e.setName((String) body.get("name"));
        e.setNaturalLanguage(body.get("naturalLanguage") != null ? body.get("naturalLanguage").toString() : "");
        e.setLogicOutput(body.get("logicOutput") != null ? body.get("logicOutput").toString() : "");
        e.setStatus(body.get("status") != null ? body.get("status").toString() : "草稿");
        e.setUpdateTime(Instant.now());
        e = repository.save(e);
        return ApiResponse.ok(toMap(e));
    }

    @PutMapping("/{id}")
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        RuleConfig e = repository.findById(id).orElse(null);
        if (e == null) return ApiResponse.fail("未找到");
        if (body.containsKey("name")) e.setName((String) body.get("name"));
        if (body.containsKey("naturalLanguage")) e.setNaturalLanguage(body.get("naturalLanguage") != null ? body.get("naturalLanguage").toString() : e.getNaturalLanguage());
        if (body.containsKey("logicOutput")) e.setLogicOutput(body.get("logicOutput") != null ? body.get("logicOutput").toString() : e.getLogicOutput());
        if (body.containsKey("status")) e.setStatus(body.get("status") != null ? body.get("status").toString() : e.getStatus());
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

    private Map<String, Object> toMap(RuleConfig e) {
        return Map.of(
                "id", String.valueOf(e.getId()),
                "name", nullToEmpty(e.getName()),
                "naturalLanguage", nullToEmpty(e.getNaturalLanguage()),
                "logicOutput", nullToEmpty(e.getLogicOutput()),
                "status", nullToEmpty(e.getStatus()),
                "updateTime", TimeFormat.format(e.getUpdateTime())
        );
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}

