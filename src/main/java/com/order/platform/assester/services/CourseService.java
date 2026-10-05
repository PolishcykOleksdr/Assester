package com.order.platform.assester.services;

import com.order.platform.assester.dto.CourseFormDTO;
import com.order.platform.assester.dto.CourseSummaryDTO;
import com.order.platform.assester.entities.Course;
import com.order.platform.assester.enums.CourseAccessType;
import com.order.platform.assester.enums.CourseStatus;
import com.order.platform.assester.repositories.CourseRepository;
import com.order.platform.assester.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CourseSummaryDTO> findMyCourses(String authorEmail) {
        return courseRepository.findAllByAuthorEmailOrderByUpdatedAtDesc(authorEmail)
                .stream().map(CourseService::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public CourseFormDTO getMyCourseForm(Long courseId, String authorEmail) {
        Course course = findOwnedCourse(courseId, authorEmail);
        ensureEditable(course);
        CourseFormDTO form = new CourseFormDTO();
        form.setTitle(course.getTitle());
        form.setDescription(course.getDescription());
        form.setAccessType(course.getAccessType());
        form.setPrice(course.getPrice());
        return form;
    }

    @Transactional
    public Long createDraft(CourseFormDTO form, String authorEmail) {
        Course course = new Course();
        course.setAuthor(userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
        applyForm(course, form);
        course.setStatus(CourseStatus.DRAFT);
        return courseRepository.save(course).getId();
    }

    @Transactional
    public void updateDraft(Long courseId, CourseFormDTO form, String authorEmail) {
        Course course = findOwnedCourse(courseId, authorEmail);
        ensureEditable(course);
        applyForm(course, form);
        course.setStatus(CourseStatus.DRAFT);
        course.setModerationComment(null);
    }

    @Transactional
    public void submitForReview(Long courseId, String authorEmail) {
        Course course = findOwnedCourse(courseId, authorEmail);
        ensureEditable(course);
        if (course.getTitle().isBlank() || course.getDescription().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please fill in all required fields");
        }
        validatePrice(course.getAccessType(), course.getPrice());
        course.setStatus(CourseStatus.PENDING_REVIEW);
        course.setModerationComment(null);
    }

    private Course findOwnedCourse(Long id, String email) {
        return courseRepository.findByIdAndAuthorEmail(id, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private void ensureEditable(Course course) {
        if (course.getStatus() != CourseStatus.DRAFT && course.getStatus() != CourseStatus.REJECTED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "You can`t edit this course right now");
        }
    }

    private void applyForm(Course course, CourseFormDTO form) {
        validatePrice(form.getAccessType(), form.getPrice());
        course.setTitle(form.getTitle().trim());
        course.setDescription(form.getDescription().trim());
        course.setAccessType(form.getAccessType());
        course.setPrice(form.getAccessType() == CourseAccessType.PAID ? form.getPrice() : null);
        course.setCurrency("UAH");
    }

    private void validatePrice(CourseAccessType accessType, java.math.BigDecimal price) {
        if (accessType == CourseAccessType.PAID && (price == null || price.signum() <= 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please specify price for paid courses");
        }
    }

    private static CourseSummaryDTO toSummary(Course course) {
        return new CourseSummaryDTO(course.getId(), course.getTitle(), course.getDescription(),
                course.getAccessType(), course.getPrice(), course.getCurrency(), course.getStatus(),
                course.getModerationComment(), course.getUpdatedAt());
    }
}
