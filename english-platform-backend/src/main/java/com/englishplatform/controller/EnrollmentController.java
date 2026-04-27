package com.englishplatform.controller;

import com.englishplatform.dto.request.EnrollmentRequest;
import com.englishplatform.dto.request.UpdateProgressRequest;
import com.englishplatform.dto.response.ApiResponse;
import com.englishplatform.dto.response.EnrollmentResponse;
import com.englishplatform.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enroll(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody EnrollmentRequest request) {
        EnrollmentResponse enrollment = enrollmentService.enroll(userDetails.getUsername(), request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Enrolled successfully", enrollment));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getMyEnrollments(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<EnrollmentResponse> enrollments = enrollmentService.getMyEnrollments(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Your enrollments retrieved", enrollments));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getAllEnrollments() {
        List<EnrollmentResponse> enrollments = enrollmentService.getAllEnrollments();
        return ResponseEntity.ok(ApiResponse.success("All enrollments retrieved", enrollments));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> getEnrollmentById(@PathVariable Long id) {
        EnrollmentResponse enrollment = enrollmentService.getEnrollmentById(id);
        return ResponseEntity.ok(ApiResponse.success("Enrollment retrieved", enrollment));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getEnrollmentsByCourse(
            @PathVariable Long courseId) {
        List<EnrollmentResponse> enrollments = enrollmentService.getEnrollmentsByCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success("Enrollments for course retrieved", enrollments));
    }

    @PatchMapping("/{id}/progress")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> updateProgress(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateProgressRequest request) {
        EnrollmentResponse enrollment = enrollmentService.updateProgress(id, userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("Progress updated", enrollment));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> cancelEnrollment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        EnrollmentResponse enrollment = enrollmentService.cancelEnrollment(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Enrollment cancelled", enrollment));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        EnrollmentResponse enrollment = enrollmentService.updateEnrollmentStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Enrollment status updated", enrollment));
    }
}
