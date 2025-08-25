package org.dimon.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ProcessingLogDto {
    private UUID id;
    private String user;
    private String inputText;
    private String outputText;
    private LocalDateTime createdAt;
}
