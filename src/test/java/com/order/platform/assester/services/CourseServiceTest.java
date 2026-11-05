package com.order.platform.assester.services;

import com.order.platform.assester.dto.CourseSummaryDTO;
import com.order.platform.assester.entities.Course;
import com.order.platform.assester.entities.User;
import com.order.platform.assester.enums.CourseAccessType;
import com.order.platform.assester.enums.CourseStatus;
import com.order.platform.assester.repositories.CourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * author: user,
 * date: 23.10.2026
 */

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {
    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    @Test
    void findMyCourses_returnsCourseSummariesForAuthor() {
        String authorEmail = "testmail@gmail.com";

        Course course = new Course();
        course.setId(1L);
        course.setCourseCode("TEST-123");
        course.setTitle("Test Course");
        course.setDescription("Test Description");
        course.setAccessType(CourseAccessType.FREE);
        course.setStatus(CourseStatus.DRAFT);
        course.setModerationComment("Please update the description");

        when(courseRepository.findAllByAuthorEmailOrderByUpdatedAtDesc(authorEmail))
                .thenReturn(List.of(course));

        List<CourseSummaryDTO> returnedCourses = courseService.findMyCourses(authorEmail);

        assertEquals(1, returnedCourses.size());

        CourseSummaryDTO summary = returnedCourses.getFirst();
        assertEquals(1L, summary.id());
        assertEquals("TEST-123", summary.courseCode());
        assertEquals("Test Course", summary.title());
        assertEquals("Test Description", summary.description());
        assertEquals(CourseAccessType.FREE, summary.accessType());
        assertEquals(CourseStatus.DRAFT, summary.status());
        assertEquals("Please update the description", summary.moderationComment());
        assertEquals(0, summary.courseMaterials().size());
        assertTrue(summary.courseMaterials().isEmpty());

        verify(courseRepository).findAllByAuthorEmailOrderByUpdatedAtDesc(authorEmail);
    }
    @Test
    void findPublishedCourses() {
    }

    @Test
    void searchPublishedCourses() {
    }

    @Test
    void findPublishedCourseByCode() {
    }

    @Test
    void getMyCourseForm() {
    }

    @Test
    void createDraft() {
    }

    @Test
    void updateDraft() {
    }

    @Test
    void submitForReview() {
    }
}