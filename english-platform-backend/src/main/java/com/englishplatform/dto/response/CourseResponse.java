package com.englishplatform.dto.response;

import com.englishplatform.entity.Level;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private Level level;
    private boolean active;
    private long lessonCount;
    private long enrollmentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
