package com.englishplatform.repository;

import com.englishplatform.entity.Course;
import com.englishplatform.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByActiveTrue();

    List<Course> findByLevel(Level level);

    List<Course> findByActiveTrueAndLevel(Level level);

    boolean existsByTitle(String title);
}
