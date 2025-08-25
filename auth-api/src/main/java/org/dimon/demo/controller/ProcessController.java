package org.dimon.demo.controller;

import org.dimon.demo.dto.ProcessRequest;
import org.dimon.demo.dto.ProcessResponse;
import org.dimon.demo.dto.ProcessingLogDto;
import org.dimon.demo.service.ProcessService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping
public class ProcessController {

    private final ProcessService processService;

    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }

    @PostMapping("/process")
    public ResponseEntity<ProcessResponse> process(@Validated @RequestBody ProcessRequest req) {
        String result = processService.processText(req.getText());
        return ResponseEntity.ok(new ProcessResponse(result));
    }

    @GetMapping("/process/logs")
    public List<ProcessingLogDto> getAllLogs() {
        return processService.getAllLogs();
    }
}

