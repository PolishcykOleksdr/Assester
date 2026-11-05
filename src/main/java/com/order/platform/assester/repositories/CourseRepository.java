package com.order.platform.assester.repositories;

import com.order.platform.assester.entities.Course;
import com.order.platform.assester.enums.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    @EntityGraph(attributePaths = {"courseMaterials", "courseMaterials.material"})
    List<Course> findAllByAuthorEmailOrderByUpdatedAtDesc(String authorEmail);

    @EntityGraph(attributePaths = {"courseMaterials", "courseMaterials.material"})
    List<Course> findAllByStatusOrderByCreatedAtDesc(CourseStatus status);

    @Query("""
    SELECT c
    FROM Course c
    WHERE c.status = CourseStatus.PUBLISHED
    AND LOWER(c.title) LIKE LOWER(CONCAT('%', :query, '%'))
    ORDER BY c.updatedAt DESC
    """)
    List<Course> findAllPublishedByQuery(
            String query);

    @Query("""
       SELECT c
       FROM Course c
       WHERE c.id = :id
       AND c.author = :email
       AND (c.status = CourseStatus.DRAFT OR c.status = CourseStatus.REJECTED)
    """)
    Optional<Course> findByIdAndAuthorEmailEditable(Long id, String email);

    @EntityGraph(attributePaths = {"courseMaterials", "courseMaterials.material"})
    Optional<Course> findByCourseCodeIgnoreCaseAndStatus(String courseCode, CourseStatus status);
}
