package com.processagent.controller;

import com.processagent.entity.ProcessPathItem;
import com.processagent.repository.ProcessPathItemRepository;
import com.processagent.util.ApiResponse;
import com.processagent.util.TimeFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/process-path")
@RequiredArgsConstructor
public class ProcessPathController {

    private final ProcessPathItemRepository repository;

    @GetMapping
    public ApiResponse<List<Map<String, Object>>> list(
            @RequestParam(required = false) String partName,
            @RequestParam(required = false) String status) {
        List<ProcessPathItem> list = repository.findAll();
        if (partName != null && !partName.isBlank()) {
            list = list.stream().filter(p -> p.getPartName() != null && p.getPartName().contains(partName)).toList();
        }
        if (status != null && !status.isBlank()) {
            list = list.stream().filter(p -> status.equals(p.getStatus())).toList();
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
        ProcessPathItem e = new ProcessPathItem();
        e.setPartName((String) body.get("partName"));
        e.setPath((String) body.get("path"));
        e.setSteps(body.get("steps") != null ? ((Number) body.get("steps")).intValue() : 0);
        e.setStatus(body.get("status") != null ? body.get("status").toString() : "已输出");
        e.setOutputTime(Instant.now());
        e = repository.save(e);
        return ApiResponse.ok(toMap(e));
    }

    @PutMapping("/{id}")
    public ApiResponse<Map<String, Object>> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        ProcessPathItem e = repository.findById(id).orElse(null);
        if (e == null) return ApiResponse.fail("未找到");
        if (body.containsKey("partName")) e.setPartName((String) body.get("partName"));
        if (body.containsKey("path")) e.setPath((String) body.get("path"));
        if (body.containsKey("steps")) e.setSteps(((Number) body.get("steps")).intValue());
        if (body.containsKey("status")) e.setStatus(body.get("status").toString());
        e = repository.save(e);
        return ApiResponse.ok(toMap(e));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) return ApiResponse.fail("未找到");
        repository.deleteById(id);
        return ApiResponse.ok(null);
    }

    private Map<String, Object> toMap(ProcessPathItem e) {
        return Map.of(
                "id", String.valueOf(e.getId()),
                "partName", nullToEmpty(e.getPartName()),
                "path", nullToEmpty(e.getPath()),
                "steps", e.getSteps() != null ? e.getSteps() : 0,
                "status", nullToEmpty(e.getStatus()),
                "outputTime", e.getOutputTime() != null ? TimeFormat.format(e.getOutputTime()) : "-"
        );
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
