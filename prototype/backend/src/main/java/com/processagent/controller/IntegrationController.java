package com.processagent.controller;

import com.processagent.entity.IntegrationTask;
import com.processagent.repository.IntegrationTaskRepository;
import com.processagent.util.ApiResponse;
import com.processagent.util.TimeFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/integration")
@RequiredArgsConstructor
public class IntegrationController {

    private final IntegrationTaskRepository repository;

    @GetMapping("/tasks")
    public ApiResponse<List<Map<String, Object>>> list(
            @RequestParam(required = false) String system,
            @RequestParam(required = false) String status) {
        List<IntegrationTask> list = repository.findAll();
        if (system != null && !system.isBlank()) {
            list = list.stream().filter(t -> system.equals(t.getSystem())).toList();
        }
        if (status != null && !status.isBlank()) {
            list = list.stream().filter(t -> status.equals(t.getStatus())).toList();
        }
        List<Map<String, Object>> data = list.stream().map(this::toMap).collect(Collectors.toList());
        return ApiResponse.ok(data);
    }

    @GetMapping("/tasks/{id}")
    public ApiResponse<Map<String, Object>> get(@PathVariable Long id) {
        return repository.findById(id)
                .map(this::toMap)
                .map(ApiResponse::ok)
                .orElse(ApiResponse.fail("未找到"));
    }

    @PostMapping("/tasks")
    public ApiResponse<Map<String, Object>> create(@RequestBody Map<String, Object> body) {
        IntegrationTask e = new IntegrationTask();
        e.setName((String) body.get("name"));
        e.setSystem((String) body.get("system"));
        e.setStatus(body.get("status") != null ? body.get("status").toString() : "排队中");
        e.setProgress(body.get("progress") != null ? ((Number) body.get("progress")).intValue() : 0);
        e.setCreateTime(Instant.now());
        if (body.get("startTime") != null && !body.get("startTime").toString().isBlank())
            e.setStartTime(Instant.parse(body.get("startTime").toString()));
        if (body.get("endTime") != null && !body.get("endTime").toString().isBlank())
            e.setEndTime(Instant.parse(body.get("endTime").toString()));
        if (body.get("runTimeSeconds") != null)
            e.setRunTimeSeconds(((Number) body.get("runTimeSeconds")).longValue());
        e = repository.save(e);
        return ApiResponse.ok(toMap(e));
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
        return Map.of(
                "id", String.valueOf(e.getId()),
                "name", nullToEmpty(e.getName()),
                "system", nullToEmpty(e.getSystem()),
                "status", nullToEmpty(e.getStatus()),
                "progress", e.getProgress() != null ? e.getProgress() : 0,
                "createTime", TimeFormat.format(e.getCreateTime()),
                "startTime", e.getStartTime() != null ? TimeFormat.format(e.getStartTime()) : "",
                "endTime", e.getEndTime() != null ? TimeFormat.format(e.getEndTime()) : "",
                "runTimeSeconds", e.getRunTimeSeconds() != null ? e.getRunTimeSeconds() : 0L
        );
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
