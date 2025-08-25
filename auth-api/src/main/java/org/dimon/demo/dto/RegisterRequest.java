package org.dimon.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequest {
    @Email
    @NotBlank
    private String username;

    @NotBlank @Size(min = 6)
    private String password;
}
