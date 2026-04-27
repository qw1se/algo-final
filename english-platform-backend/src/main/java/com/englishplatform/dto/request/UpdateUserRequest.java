package com.englishplatform.dto.request;

import com.englishplatform.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {

    @Email(message = "Invalid email format")
    private String email;

    @Size(max = 100)
    private String fullName;

    private Role role;

    private Boolean enabled;
}
