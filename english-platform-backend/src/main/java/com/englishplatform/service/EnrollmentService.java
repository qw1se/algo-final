package com.englishplatform.service;

import com.englishplatform.dto.request.EnrollmentRequest;
import com.englishplatform.dto.request.UpdateProgressRequest;
import com.englishplatform.dto.response.EnrollmentResponse;

import java.util.List;

public interface EnrollmentService {
    EnrollmentResponse enroll(String username, EnrollmentRequest request);
    List<EnrollmentResponse> getMyEnrollments(String username);
    List<EnrollmentResponse> getEnrollmentsByCourse(Long courseId);
    List<EnrollmentResponse> getAllEnrollments();
    EnrollmentResponse getEnrollmentById(Long id);
    EnrollmentResponse updateProgress(Long id, String username, UpdateProgressRequest request);
    EnrollmentResponse cancelEnrollment(Long id, String username);
    EnrollmentResponse updateEnrollmentStatus(Long id, String status);
}
