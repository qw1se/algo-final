package com.englishplatform.service;

import com.englishplatform.dto.request.CourseRequest;
import com.englishplatform.dto.response.CourseResponse;

import java.util.List;

public interface CourseService {
    List<CourseResponse> getAllCourses();
    List<CourseResponse> getActiveCourses();
    CourseResponse getCourseById(Long id);
    List<CourseResponse> getCoursesByLevel(String level);
    CourseResponse createCourse(CourseRequest request);
    CourseResponse updateCourse(Long id, CourseRequest request);
    void deleteCourse(Long id);
    CourseResponse toggleCourseStatus(Long id);
}
