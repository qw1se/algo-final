package com.englishplatform.service;

import com.englishplatform.dto.request.CourseRequest;
import com.englishplatform.dto.response.CourseResponse;
import com.englishplatform.entity.Course;
import com.englishplatform.entity.Level;
import com.englishplatform.exception.DuplicateResourceException;
import com.englishplatform.exception.ResourceNotFoundException;
import com.englishplatform.repository.CourseRepository;
import com.englishplatform.repository.EnrollmentRepository;
import com.englishplatform.repository.LessonRepository;
import com.englishplatform.service.impl.CourseServiceImpl;
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
@DisplayName("CourseService Tests")
class CourseServiceImplTest {

    @Mock private CourseRepository courseRepository;
    @Mock private LessonRepository lessonRepository;
    @Mock private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Course course;
    private CourseRequest courseRequest;

    @BeforeEach
    void setUp() {
        course = Course.builder()
                .id(1L)
                .title("English for Beginners")
                .description("Basic English course")
                .level(Level.BEGINNER)
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        courseRequest = new CourseRequest();
        courseRequest.setTitle("English for Beginners");
        courseRequest.setDescription("Basic English course");
        courseRequest.setLevel(Level.BEGINNER);
        courseRequest.setActive(true);
    }

    @Test
    @DisplayName("getAllCourses() - returns list of all courses")
    void getAllCourses_ReturnsList() {
        when(courseRepository.findAll()).thenReturn(List.of(course));
        when(lessonRepository.countByCourseId(1L)).thenReturn(5L);
        when(enrollmentRepository.countActiveEnrollmentsByCourse(1L)).thenReturn(10L);

        List<CourseResponse> result = courseService.getAllCourses();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("English for Beginners");
        assertThat(result.get(0).getLessonCount()).isEqualTo(5L);
        assertThat(result.get(0).getEnrollmentCount()).isEqualTo(10L);
    }

    @Test
    @DisplayName("getCourseById() - existing course returns response")
    void getCourseById_Exists_ReturnsResponse() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(lessonRepository.countByCourseId(1L)).thenReturn(3L);
        when(enrollmentRepository.countActiveEnrollmentsByCourse(1L)).thenReturn(7L);

        CourseResponse result = courseService.getCourseById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("English for Beginners");
        assertThat(result.getLevel()).isEqualTo(Level.BEGINNER);
    }

    @Test
    @DisplayName("getCourseById() - non-existing throws ResourceNotFoundException")
    void getCourseById_NotFound_ThrowsException() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> courseService.getCourseById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("createCourse() - success")
    void createCourse_Success() {
        when(courseRepository.existsByTitle("English for Beginners")).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenReturn(course);
        when(lessonRepository.countByCourseId(1L)).thenReturn(0L);
        when(enrollmentRepository.countActiveEnrollmentsByCourse(1L)).thenReturn(0L);

        CourseResponse result = courseService.createCourse(courseRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("English for Beginners");
        verify(courseRepository).save(any(Course.class));
    }

    @Test
    @DisplayName("createCourse() - duplicate title throws DuplicateResourceException")
    void createCourse_DuplicateTitle_ThrowsException() {
        when(courseRepository.existsByTitle("English for Beginners")).thenReturn(true);

        assertThatThrownBy(() -> courseService.createCourse(courseRequest))
                .isInstanceOf(DuplicateResourceException.class);

        verify(courseRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateCourse() - success updates fields")
    void updateCourse_Success() {
        CourseRequest updateRequest = new CourseRequest();
        updateRequest.setTitle("Advanced English");
        updateRequest.setDescription("For advanced learners");
        updateRequest.setLevel(Level.ADVANCED);
        updateRequest.setActive(true);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.existsByTitle("Advanced English")).thenReturn(false);
        when(courseRepository.save(any(Course.class))).thenAnswer(i -> i.getArgument(0));
        when(lessonRepository.countByCourseId(any())).thenReturn(0L);
        when(enrollmentRepository.countActiveEnrollmentsByCourse(any())).thenReturn(0L);

        CourseResponse result = courseService.updateCourse(1L, updateRequest);

        assertThat(result.getTitle()).isEqualTo("Advanced English");
        assertThat(result.getLevel()).isEqualTo(Level.ADVANCED);
    }

    @Test
    @DisplayName("deleteCourse() - success")
    void deleteCourse_Success() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        courseService.deleteCourse(1L);

        verify(courseRepository).delete(course);
    }

    @Test
    @DisplayName("toggleCourseStatus() - toggles active flag")
    void toggleCourseStatus_TogglesActive() {
        course.setActive(true);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.save(any(Course.class))).thenAnswer(i -> i.getArgument(0));
        when(lessonRepository.countByCourseId(any())).thenReturn(0L);
        when(enrollmentRepository.countActiveEnrollmentsByCourse(any())).thenReturn(0L);

        CourseResponse result = courseService.toggleCourseStatus(1L);

        assertThat(result.isActive()).isFalse();
    }

    @Test
    @DisplayName("getCoursesByLevel() - invalid level throws IllegalArgumentException")
    void getCoursesByLevel_InvalidLevel_Throws() {
        assertThatThrownBy(() -> courseService.getCoursesByLevel("INVALID_LEVEL"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid level");
    }
}
