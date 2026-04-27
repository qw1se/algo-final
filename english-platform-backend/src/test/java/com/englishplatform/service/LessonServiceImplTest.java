package com.englishplatform.service;

import com.englishplatform.dto.request.LessonRequest;
import com.englishplatform.dto.response.LessonResponse;
import com.englishplatform.entity.Course;
import com.englishplatform.entity.Level;
import com.englishplatform.entity.Lesson;
import com.englishplatform.exception.DuplicateResourceException;
import com.englishplatform.exception.ResourceNotFoundException;
import com.englishplatform.repository.CourseRepository;
import com.englishplatform.repository.LessonRepository;
import com.englishplatform.service.impl.LessonServiceImpl;
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
@DisplayName("LessonService Tests")
class LessonServiceImplTest {

    @Mock private LessonRepository lessonRepository;
    @Mock private CourseRepository courseRepository;

    @InjectMocks
    private LessonServiceImpl lessonService;

    private Course course;
    private Lesson lesson;
    private LessonRequest lessonRequest;

    @BeforeEach
    void setUp() {
        course = Course.builder()
                .id(1L)
                .title("English for Beginners")
                .level(Level.BEGINNER)
                .active(true)
                .build();

        lesson = Lesson.builder()
                .id(1L)
                .title("Lesson 1: Greetings")
                .content("In this lesson we learn greetings.")
                .orderIndex(1)
                .durationMinutes(30)
                .published(false)
                .course(course)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        lessonRequest = new LessonRequest();
        lessonRequest.setTitle("Lesson 1: Greetings");
        lessonRequest.setContent("In this lesson we learn greetings.");
        lessonRequest.setOrderIndex(1);
        lessonRequest.setDurationMinutes(30);
        lessonRequest.setPublished(false);
        lessonRequest.setCourseId(1L);
    }

    @Test
    @DisplayName("getLessonsByCourse() - returns ordered list")
    void getLessonsByCourse_ReturnsList() {
        when(courseRepository.existsById(1L)).thenReturn(true);
        when(lessonRepository.findByCourseIdOrderByOrderIndex(1L)).thenReturn(List.of(lesson));

        List<LessonResponse> result = lessonService.getLessonsByCourse(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Lesson 1: Greetings");
        assertThat(result.get(0).getOrderIndex()).isEqualTo(1);
    }

    @Test
    @DisplayName("getLessonsByCourse() - course not found throws ResourceNotFoundException")
    void getLessonsByCourse_CourseNotFound_Throws() {
        when(courseRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> lessonService.getLessonsByCourse(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("getLessonById() - existing lesson returns response")
    void getLessonById_Exists_ReturnsResponse() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));

        LessonResponse result = lessonService.getLessonById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCourseId()).isEqualTo(1L);
        assertThat(result.getCourseTitle()).isEqualTo("English for Beginners");
    }

    @Test
    @DisplayName("getLessonById() - not found throws ResourceNotFoundException")
    void getLessonById_NotFound_Throws() {
        when(lessonRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lessonService.getLessonById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("createLesson() - success")
    void createLesson_Success() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(lessonRepository.existsByOrderIndexAndCourseId(1, 1L)).thenReturn(false);
        when(lessonRepository.save(any(Lesson.class))).thenReturn(lesson);

        LessonResponse result = lessonService.createLesson(lessonRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Lesson 1: Greetings");
        verify(lessonRepository).save(any(Lesson.class));
    }

    @Test
    @DisplayName("createLesson() - duplicate orderIndex throws DuplicateResourceException")
    void createLesson_DuplicateOrder_Throws() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(lessonRepository.existsByOrderIndexAndCourseId(1, 1L)).thenReturn(true);

        assertThatThrownBy(() -> lessonService.createLesson(lessonRequest))
                .isInstanceOf(DuplicateResourceException.class);

        verify(lessonRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteLesson() - success")
    void deleteLesson_Success() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));

        lessonService.deleteLesson(1L);

        verify(lessonRepository).delete(lesson);
    }

    @Test
    @DisplayName("togglePublishStatus() - toggles published flag")
    void togglePublishStatus_Toggles() {
        lesson.setPublished(false);
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));
        when(lessonRepository.save(any(Lesson.class))).thenAnswer(i -> i.getArgument(0));

        LessonResponse result = lessonService.togglePublishStatus(1L);

        assertThat(result.isPublished()).isTrue();
    }
}
