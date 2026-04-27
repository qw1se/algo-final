package com.englishplatform.dto.request;

import com.englishplatform.entity.Level;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CourseRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must not exceed 150 characters")
    private String title;

    private String description;

    @NotNull(message = "Level is required")
    private Level level;

    private boolean active = true;
}
