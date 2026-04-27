package com.englishplatform.dto.response;

import com.englishplatform.entity.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private Long id;
    private Long userId;
    private String username;
    private Long courseId;
    private String courseTitle;
    private EnrollmentStatus status;
    private Integer progressPercent;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
}
