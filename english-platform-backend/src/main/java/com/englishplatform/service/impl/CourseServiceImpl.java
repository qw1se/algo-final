package com.englishplatform.service.impl;

import com.englishplatform.dto.request.CourseRequest;
import com.englishplatform.dto.response.CourseResponse;
import com.englishplatform.entity.Course;
import com.englishplatform.entity.Level;
import com.englishplatform.exception.DuplicateResourceException;
import com.englishplatform.exception.ResourceNotFoundException;
import com.englishplatform.repository.CourseRepository;
import com.englishplatform.repository.EnrollmentRepository;
import com.englishplatform.repository.LessonRepository;
import com.englishplatform.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getActiveCourses() {
        return courseRepository.findByActiveTrue().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        return mapToResponse(findCourseById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesByLevel(String level) {
        Level levelEnum;
        try {
            levelEnum = Level.valueOf(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid level: " + level + ". Valid values: BEGINNER, ELEMENTARY, INTERMEDIATE, UPPER_INTERMEDIATE, ADVANCED, PROFICIENCY");
        }
        return courseRepository.findByActiveTrueAndLevel(levelEnum).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        if (courseRepository.existsByTitle(request.getTitle())) {
            throw new DuplicateResourceException("Course with title '" + request.getTitle() + "' already exists");
        }
        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .level(request.getLevel())
                .active(request.isActive())
                .build();
        Course saved = courseRepository.save(course);
        log.info("Course created: {}", saved.getId());
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        Course course = findCourseById(id);
        if (!course.getTitle().equals(request.getTitle()) && courseRepository.existsByTitle(request.getTitle())) {
            throw new DuplicateResourceException("Course title already exists: " + request.getTitle());
        }
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setLevel(request.getLevel());
        course.setActive(request.isActive());
        return mapToResponse(courseRepository.save(course));
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        Course course = findCourseById(id);
        courseRepository.delete(course);
        log.info("Deleted course with id: {}", id);
    }

    @Override
    @Transactional
    public CourseResponse toggleCourseStatus(Long id) {
        Course course = findCourseById(id);
        course.setActive(!course.isActive());
        return mapToResponse(courseRepository.save(course));
    }

    private Course findCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    public CourseResponse mapToResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .level(course.getLevel())
                .active(course.isActive())
                .lessonCount(lessonRepository.countByCourseId(course.getId()))
                .enrollmentCount(enrollmentRepository.countActiveEnrollmentsByCourse(course.getId()))
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }
}
