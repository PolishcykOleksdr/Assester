package com.order.platform.assester.repositories;

import com.order.platform.assester.entities.Course;
import com.order.platform.assester.enums.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findAllByAuthorEmailOrderByUpdatedAtDesc(String email);
    List<Course> findAllByStatusOrderByUpdatedAtDesc(CourseStatus status);
    List<Course> findAllByStatusAndTitleContainingIgnoreCaseOrStatusAndDescriptionContainingIgnoreCaseOrderByUpdatedAtDesc(
            CourseStatus titleStatus, String title, CourseStatus descriptionStatus, String description);
    Optional<Course> findByIdAndAuthorEmail(Long id, String email);
    Optional<Course> findByCourseCodeIgnoreCaseAndStatus(String courseCode, CourseStatus status);
}
