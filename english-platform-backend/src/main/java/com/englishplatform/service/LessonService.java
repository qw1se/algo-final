package com.englishplatform.service;

import com.englishplatform.dto.request.LessonRequest;
import com.englishplatform.dto.response.LessonResponse;

import java.util.List;

public interface LessonService {
    List<LessonResponse> getLessonsByCourse(Long courseId);
    List<LessonResponse> getPublishedLessonsByCourse(Long courseId);
    LessonResponse getLessonById(Long id);
    LessonResponse createLesson(LessonRequest request);
    LessonResponse updateLesson(Long id, LessonRequest request);
    void deleteLesson(Long id);
    LessonResponse togglePublishStatus(Long id);
}
