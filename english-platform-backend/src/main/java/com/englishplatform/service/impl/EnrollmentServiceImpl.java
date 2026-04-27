package com.englishplatform.service.impl;

import com.englishplatform.dto.request.EnrollmentRequest;
import com.englishplatform.dto.request.UpdateProgressRequest;
import com.englishplatform.dto.response.EnrollmentResponse;
import com.englishplatform.entity.*;
import com.englishplatform.exception.AccessDeniedException;
import com.englishplatform.exception.DuplicateResourceException;
import com.englishplatform.exception.ResourceNotFoundException;
import com.englishplatform.repository.CourseRepository;
import com.englishplatform.repository.EnrollmentRepository;
import com.englishplatform.repository.UserRepository;
import com.englishplatform.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public EnrollmentResponse enroll(String username, EnrollmentRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + request.getCourseId()));

        if (!course.isActive()) {
            throw new IllegalArgumentException("Cannot enroll in an inactive course");
        }
        if (enrollmentRepository.existsByUserIdAndCourseId(user.getId(), course.getId())) {
            throw new DuplicateResourceException("Already enrolled in course: " + course.getTitle());
        }

        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .status(EnrollmentStatus.ACTIVE)
                .progressPercent(0)
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);
        log.info("User {} enrolled in course {}", username, course.getId());
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getMyEnrollments(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        return enrollmentRepository.findByUserId(user.getId()).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getEnrollmentsByCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found: " + courseId);
        }
        return enrollmentRepository.findByCourseId(courseId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponse> getAllEnrollments() {
        return enrollmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public EnrollmentResponse getEnrollmentById(Long id) {
        return mapToResponse(findEnrollmentById(id));
    }

    @Override
    @Transactional
    public EnrollmentResponse updateProgress(Long id, String username, UpdateProgressRequest request) {
        Enrollment enrollment = findEnrollmentById(id);
        if (!enrollment.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You can only update your own enrollment progress");
        }
        enrollment.setProgressPercent(request.getProgressPercent());
        if (request.getProgressPercent() == 100) {
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollment.setCompletedAt(LocalDateTime.now());
            log.info("User {} completed course {}", username, enrollment.getCourse().getId());
        }
        return mapToResponse(enrollmentRepository.save(enrollment));
    }

    @Override
    @Transactional
    public EnrollmentResponse cancelEnrollment(Long id, String username) {
        Enrollment enrollment = findEnrollmentById(id);
        if (!enrollment.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("You can only cancel your own enrollment");
        }
        enrollment.setStatus(EnrollmentStatus.CANCELLED);
        return mapToResponse(enrollmentRepository.save(enrollment));
    }

    @Override
    @Transactional
    public EnrollmentResponse updateEnrollmentStatus(Long id, String status) {
        Enrollment enrollment = findEnrollmentById(id);
        try {
            EnrollmentStatus newStatus = EnrollmentStatus.valueOf(status.toUpperCase());
            enrollment.setStatus(newStatus);
            if (newStatus == EnrollmentStatus.COMPLETED && enrollment.getCompletedAt() == null) {
                enrollment.setCompletedAt(LocalDateTime.now());
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
        return mapToResponse(enrollmentRepository.save(enrollment));
    }

    private Enrollment findEnrollmentById(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found with id: " + id));
    }

    public EnrollmentResponse mapToResponse(Enrollment e) {
        return EnrollmentResponse.builder()
                .id(e.getId())
                .userId(e.getUser().getId())
                .username(e.getUser().getUsername())
                .courseId(e.getCourse().getId())
                .courseTitle(e.getCourse().getTitle())
                .status(e.getStatus())
                .progressPercent(e.getProgressPercent())
                .enrolledAt(e.getEnrolledAt())
                .completedAt(e.getCompletedAt())
                .build();
    }
}
