package com.englishplatform.service.impl;

import com.englishplatform.dto.request.LessonRequest;
import com.englishplatform.dto.response.LessonResponse;
import com.englishplatform.entity.Course;
import com.englishplatform.entity.Lesson;
import com.englishplatform.exception.DuplicateResourceException;
import com.englishplatform.exception.ResourceNotFoundException;
import com.englishplatform.repository.CourseRepository;
import com.englishplatform.repository.LessonRepository;
import com.englishplatform.service.LessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<LessonResponse> getLessonsByCourse(Long courseId) {
        ensureCourseExists(courseId);
        return lessonRepository.findByCourseIdOrderByOrderIndex(courseId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonResponse> getPublishedLessonsByCourse(Long courseId) {
        ensureCourseExists(courseId);
        return lessonRepository.findByCourseIdAndPublishedTrue(courseId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LessonResponse getLessonById(Long id) {
        return mapToResponse(findLessonById(id));
    }

    @Override
    @Transactional
    public LessonResponse createLesson(LessonRequest request) {
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.getCourseId()));

        if (lessonRepository.existsByOrderIndexAndCourseId(request.getOrderIndex(), request.getCourseId())) {
            throw new DuplicateResourceException("A lesson with order index " + request.getOrderIndex()
                    + " already exists in this course");
        }

        Lesson lesson = Lesson.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .orderIndex(request.getOrderIndex())
                .durationMinutes(request.getDurationMinutes())
                .published(request.isPublished())
                .course(course)
                .build();

        Lesson saved = lessonRepository.save(lesson);
        log.info("Lesson created: {} in course: {}", saved.getId(), course.getId());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public LessonResponse updateLesson(Long id, LessonRequest request) {
        Lesson lesson = findLessonById(id);
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + request.getCourseId()));

        boolean orderChanged = !lesson.getOrderIndex().equals(request.getOrderIndex());
        boolean courseChanged = !lesson.getCourse().getId().equals(request.getCourseId());
        if ((orderChanged || courseChanged)
                && lessonRepository.existsByOrderIndexAndCourseId(request.getOrderIndex(), request.getCourseId())) {
            throw new DuplicateResourceException("Order index " + request.getOrderIndex() + " already taken in that course");
        }

        lesson.setTitle(request.getTitle());
        lesson.setContent(request.getContent());
        lesson.setOrderIndex(request.getOrderIndex());
        lesson.setDurationMinutes(request.getDurationMinutes());
        lesson.setPublished(request.isPublished());
        lesson.setCourse(course);

        return mapToResponse(lessonRepository.save(lesson));
    }

    @Override
    @Transactional
    public void deleteLesson(Long id) {
        Lesson lesson = findLessonById(id);
        lessonRepository.delete(lesson);
        log.info("Deleted lesson with id: {}", id);
    }

    @Override
    @Transactional
    public LessonResponse togglePublishStatus(Long id) {
        Lesson lesson = findLessonById(id);
        lesson.setPublished(!lesson.isPublished());
        return mapToResponse(lessonRepository.save(lesson));
    }

    private Lesson findLessonById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found with id: " + id));
    }

    private void ensureCourseExists(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with id: " + courseId);
        }
    }

    public LessonResponse mapToResponse(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .orderIndex(lesson.getOrderIndex())
                .durationMinutes(lesson.getDurationMinutes())
                .published(lesson.isPublished())
                .courseId(lesson.getCourse().getId())
                .courseTitle(lesson.getCourse().getTitle())
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt())
                .build();
    }
}
