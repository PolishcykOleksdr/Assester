package com.order.platform.assester.repositories;

import com.order.platform.assester.entities.Course;
import com.order.platform.assester.enums.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    @EntityGraph(attributePaths = {"courseMaterials", "courseMaterials.material"})
    List<Course> findAllByAuthorEmailOrderByUpdatedAtDesc(String email);
    @EntityGraph(attributePaths = {"courseMaterials", "courseMaterials.material"})
    List<Course> findAllByStatusOrderByUpdatedAtDesc(CourseStatus status);
    List<Course> findAllByStatusAndTitleContainingIgnoreCaseOrStatusAndDescriptionContainingIgnoreCaseOrderByUpdatedAtDesc(
            CourseStatus titleStatus, String title, CourseStatus descriptionStatus, String description);
    Optional<Course> findByIdAndAuthorEmail(Long id, String email);
    @EntityGraph(attributePaths = {"courseMaterials", "courseMaterials.material"})
    Optional<Course> findByCourseCodeIgnoreCaseAndStatus(String courseCode, CourseStatus status);
}
