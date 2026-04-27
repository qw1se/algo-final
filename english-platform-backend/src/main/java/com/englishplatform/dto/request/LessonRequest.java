package com.englishplatform.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LessonRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title must not exceed 150 characters")
    private String title;

    private String content;

    @NotNull(message = "Order index is required")
    @Min(value = 1, message = "Order index must be at least 1")
    private Integer orderIndex;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private Integer durationMinutes;

    private boolean published = false;

    @NotNull(message = "Course ID is required")
    private Long courseId;
}
