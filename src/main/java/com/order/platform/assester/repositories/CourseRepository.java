package com.order.platform.assester.repositories;

import com.order.platform.assester.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findAllByAuthorEmailOrderByUpdatedAtDesc(String email);
    Optional<Course> findByIdAndAuthorEmail(Long id, String email);
}
