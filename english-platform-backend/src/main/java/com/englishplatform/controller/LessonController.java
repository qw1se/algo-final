package com.englishplatform.controller;

import com.englishplatform.dto.request.LessonRequest;
import com.englishplatform.dto.response.ApiResponse;
import com.englishplatform.dto.response.LessonResponse;
import com.englishplatform.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<LessonResponse>>> getPublishedLessons(
            @PathVariable Long courseId) {
        List<LessonResponse> lessons = lessonService.getPublishedLessonsByCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success("Published lessons retrieved", lessons));
    }

    @GetMapping("/course/{courseId}/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<LessonResponse>>> getAllLessonsByCourse(
            @PathVariable Long courseId) {
        List<LessonResponse> lessons = lessonService.getLessonsByCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success("All lessons retrieved", lessons));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LessonResponse>> getLessonById(@PathVariable Long id) {
        LessonResponse lesson = lessonService.getLessonById(id);
        return ResponseEntity.ok(ApiResponse.success("Lesson retrieved", lesson));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<LessonResponse>> createLesson(
            @Valid @RequestBody LessonRequest request) {
        LessonResponse lesson = lessonService.createLesson(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Lesson created", lesson));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<LessonResponse>> updateLesson(
            @PathVariable Long id,
            @Valid @RequestBody LessonRequest request) {
        LessonResponse updated = lessonService.updateLesson(id, request);
        return ResponseEntity.ok(ApiResponse.success("Lesson updated", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteLesson(@PathVariable Long id) {
        lessonService.deleteLesson(id);
        return ResponseEntity.ok(ApiResponse.success("Lesson deleted", null));
    }

    @PatchMapping("/{id}/toggle-publish")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<LessonResponse>> togglePublish(@PathVariable Long id) {
        LessonResponse lesson = lessonService.togglePublishStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Lesson publish status toggled", lesson));
    }
}
