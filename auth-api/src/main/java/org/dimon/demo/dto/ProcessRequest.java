package org.dimon.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProcessRequest {
    @NotBlank
    private String text;
}
