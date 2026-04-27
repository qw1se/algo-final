package com.englishplatform.repository;

import com.englishplatform.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findByCourseIdOrderByOrderIndex(Long courseId);

    List<Lesson> findByCourseIdAndPublishedTrue(Long courseId);

    long countByCourseId(Long courseId);

    boolean existsByOrderIndexAndCourseId(Integer orderIndex, Long courseId);
}
