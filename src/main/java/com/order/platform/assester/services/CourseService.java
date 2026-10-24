package com.order.platform.assester.services;

import com.order.platform.assester.dto.CourseFormDTO;
import com.order.platform.assester.dto.CourseSummaryDTO;
import com.order.platform.assester.entities.Course;
import com.order.platform.assester.entities.CourseMaterial;
import com.order.platform.assester.entities.Material;
import com.order.platform.assester.entities.User;
import com.order.platform.assester.enums.CourseAccessType;
import com.order.platform.assester.enums.CourseStatus;
import com.order.platform.assester.exceptions.course.CourseNotFoundExcpetion;
import com.order.platform.assester.exceptions.course.InvalidCourseException;
import com.order.platform.assester.mapper.CourseMapper;
import com.order.platform.assester.repositories.CourseRepository;
import com.order.platform.assester.repositories.UserRepository;
import com.order.platform.assester.repositories.MaterialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final UserService userService;

    private final CourseMapper courseMapper;

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final MaterialRepository materialRepository;

    @Transactional(readOnly = true)
    public List<CourseSummaryDTO> findMyCourses(String authorEmail) {
        List<Course> courses = courseRepository.findAllByAuthorEmailOrderByUpdatedAtDesc(authorEmail);
        return courses.stream().map(courseMapper::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public List<CourseSummaryDTO> findPublishedCourses() {
        List<Course> courses = courseRepository.findAllByStatusOrderByCreatedAtDesc(CourseStatus.PUBLISHED);
        return courses.stream().map(courseMapper::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public List<CourseSummaryDTO> searchPublishedCoursesByQuery(String query) {
        String normalizedQuery = query.trim();

        List<Course> courses = courseRepository.findAllPublishedByQuery(
                normalizedQuery
        );

        return courses.stream().map(courseMapper::toSummary).toList();
    }

    @Transactional(readOnly = true)
    public CourseSummaryDTO findPublishedByCode(String courseCode) {
        return courseRepository.findByCourseCodeIgnoreCaseAndStatus(
                courseCode.trim(), CourseStatus.PUBLISHED)
                .map(courseMapper::toSummary)
                .orElseThrow(() ->
                        new CourseNotFoundExcpetion("Course with code " + courseCode + " not found")
                );
    }

    @Transactional(readOnly = true)
    public CourseFormDTO getMyCourseForm(Long courseId, String authorEmail) {
        Course course = findOwnedEditableCourse(courseId, authorEmail);
        return courseMapper.toForm(course);
    }

    @Transactional
    public void createDraft(CourseFormDTO form, String authorEmail) {
        Course course = new Course();

        User author = userService.findByEmail(authorEmail);
        course.setAuthor(author);

        applyForm(course, form);

        courseRepository.save(course);
    }

    @Transactional
    public void updateDraft(Long courseId, CourseFormDTO form, String authorEmail) {
        Course course = findOwnedEditableCourse(courseId, authorEmail);

        course.setModerationComment(null);

        applyForm(course, form);

        courseRepository.save(course);
    }

    @Transactional
    public void submitForReview(Long courseId, String authorEmail) {
        Course course = findOwnedEditableCourse(courseId, authorEmail);

        validatePrice(course.getAccessType(), course.getPrice());

        course.setStatus(CourseStatus.PENDING_REVIEW);
        course.setModerationComment(null);

        courseRepository.save(course);
    }

    private Course findOwnedEditableCourse(Long id, String email) {
        return courseRepository.findByIdAndAuthorEmailEditable(id, email)
                .orElseThrow(() -> new CourseNotFoundExcpetion("Cannot find any editable course with id -  " + id));
    }

    private void applyForm(Course course, CourseFormDTO form) {
        validatePrice(form.getAccessType(), form.getPrice());

        course.setStatus(CourseStatus.DRAFT);
        course.setTitle(form.getTitle().trim());
        course.setDescription(form.getDescription().trim());
        course.setAccessType(form.getAccessType());
        course.setPrice(form.getAccessType() == CourseAccessType.PAID ? form.getPrice() : null);
        course.setCurrency("UAH");

        List<Long> ids = form.getMaterialIds() == null ?
                List.of() :
                form.getMaterialIds().stream().distinct().toList();

        course.getCourseMaterials().clear();

        if(form.getMaterialIds() == null){
            return;
        }

        List<Material> materials = materialRepository.findPublishedAuthorMaterialsByIds(
                ids,
                course.getAuthor().getEmail()
        );
        for (int index = 0; index < ids.size(); index++) {
            Long id = ids.get(index);
            Material material = materials
                    .stream()
                    .filter(item -> item.getId().equals(id))
                    .findFirst()
                    .orElseThrow();
            CourseMaterial link = new CourseMaterial();
            link.setCourse(course);
            link.setMaterial(material);
            link.setPosition(index);
            course.getCourseMaterials().add(link);
        }
    }

    private void validatePrice(CourseAccessType accessType, java.math.BigDecimal price) {
        if (accessType == CourseAccessType.PAID && (price == null || price.signum() <= 0)) {
            throw new InvalidCourseException("Please specify price for paid courses");
        }
    }
}
