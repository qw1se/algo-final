package com.englishplatform.service;

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
import com.englishplatform.service.impl.EnrollmentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EnrollmentService Tests")
class EnrollmentServiceImplTest {

    @Mock private EnrollmentRepository enrollmentRepository;
    @Mock private UserRepository userRepository;
    @Mock private CourseRepository courseRepository;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;

    private User user;
    private Course course;
    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .username("john_doe")
                .email("john@example.com")
                .role(Role.USER)
                .enabled(true)
                .build();

        course = Course.builder()
                .id(1L)
                .title("English for Beginners")
                .level(Level.BEGINNER)
                .active(true)
                .build();

        enrollment = Enrollment.builder()
                .id(1L)
                .user(user)
                .course(course)
                .status(EnrollmentStatus.ACTIVE)
                .progressPercent(0)
                .enrolledAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("enroll() - success creates enrollment")
    void enroll_Success() {
        EnrollmentRequest request = new EnrollmentRequest();
        request.setCourseId(1L);

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByUserIdAndCourseId(1L, 1L)).thenReturn(false);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);

        EnrollmentResponse result = enrollmentService.enroll("john_doe", request);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("john_doe");
        assertThat(result.getCourseId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
    }

    @Test
    @DisplayName("enroll() - already enrolled throws DuplicateResourceException")
    void enroll_AlreadyEnrolled_Throws() {
        EnrollmentRequest request = new EnrollmentRequest();
        request.setCourseId(1L);

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByUserIdAndCourseId(1L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> enrollmentService.enroll("john_doe", request))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("enroll() - inactive course throws IllegalArgumentException")
    void enroll_InactiveCourse_Throws() {
        course.setActive(false);
        EnrollmentRequest request = new EnrollmentRequest();
        request.setCourseId(1L);

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        assertThatThrownBy(() -> enrollmentService.enroll("john_doe", request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inactive");
    }

    @Test
    @DisplayName("getMyEnrollments() - returns user's enrollments")
    void getMyEnrollments_ReturnsList() {
        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(user));
        when(enrollmentRepository.findByUserId(1L)).thenReturn(List.of(enrollment));

        List<EnrollmentResponse> result = enrollmentService.getMyEnrollments("john_doe");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("john_doe");
    }

    @Test
    @DisplayName("updateProgress() - success updates progress")
    void updateProgress_Success() {
        UpdateProgressRequest request = new UpdateProgressRequest();
        request.setProgressPercent(50);

        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(i -> i.getArgument(0));

        EnrollmentResponse result = enrollmentService.updateProgress(1L, "john_doe", request);

        assertThat(result.getProgressPercent()).isEqualTo(50);
        assertThat(result.getStatus()).isEqualTo(EnrollmentStatus.ACTIVE);
    }

    @Test
    @DisplayName("updateProgress() - 100% marks enrollment as COMPLETED")
    void updateProgress_100Percent_MarksCompleted() {
        UpdateProgressRequest request = new UpdateProgressRequest();
        request.setProgressPercent(100);

        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(i -> i.getArgument(0));

        EnrollmentResponse result = enrollmentService.updateProgress(1L, "john_doe", request);

        assertThat(result.getProgressPercent()).isEqualTo(100);
        assertThat(result.getStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
    }

    @Test
    @DisplayName("updateProgress() - wrong user throws AccessDeniedException")
    void updateProgress_WrongUser_ThrowsAccessDenied() {
        UpdateProgressRequest request = new UpdateProgressRequest();
        request.setProgressPercent(50);

        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        assertThatThrownBy(() -> enrollmentService.updateProgress(1L, "other_user", request))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("cancelEnrollment() - success sets CANCELLED status")
    void cancelEnrollment_Success() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(i -> i.getArgument(0));

        EnrollmentResponse result = enrollmentService.cancelEnrollment(1L, "john_doe");

        assertThat(result.getStatus()).isEqualTo(EnrollmentStatus.CANCELLED);
    }

    @Test
    @DisplayName("cancelEnrollment() - wrong user throws AccessDeniedException")
    void cancelEnrollment_WrongUser_ThrowsAccessDenied() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        assertThatThrownBy(() -> enrollmentService.cancelEnrollment(1L, "hacker"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("updateEnrollmentStatus() - invalid status throws IllegalArgumentException")
    void updateEnrollmentStatus_InvalidStatus_Throws() {
        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        assertThatThrownBy(() -> enrollmentService.updateEnrollmentStatus(1L, "FLYING"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid status");
    }

    @Test
    @DisplayName("getEnrollmentById() - not found throws ResourceNotFoundException")
    void getEnrollmentById_NotFound_Throws() {
        when(enrollmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> enrollmentService.getEnrollmentById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
