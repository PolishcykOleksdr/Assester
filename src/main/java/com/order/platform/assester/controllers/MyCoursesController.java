package com.order.platform.assester.controllers;

import com.order.platform.assester.dto.CourseFormDTO;
import com.order.platform.assester.logging.annotation.Audited;
import com.order.platform.assester.services.CourseService;
import com.order.platform.assester.services.MaterialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/my/courses")
@RequiredArgsConstructor
public class MyCoursesController {
    private final CourseService courseService;
    private final MaterialService materialService;

    @Audited
    @GetMapping
    public String list(Authentication authentication, Model model) {
        model.addAttribute("courses", courseService.findMyCourses(authentication.getName()));
        return "my-courses";
    }

    @Audited
    @GetMapping("/new")
    public String newCourse(Authentication authentication, Model model) {
        model.addAttribute("materials", materialService.findMyPublishedForSelection(authentication.getName()));
        model.addAttribute("courseForm", new CourseFormDTO());
        model.addAttribute("formAction", "/my/courses");
        return "course-form";
    }

    @Audited
    @PostMapping
    public String create(@Valid @ModelAttribute("courseForm") CourseFormDTO form,
                         BindingResult bindingResult, Authentication authentication, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("materials", materialService.findMyPublishedForSelection(authentication.getName()));
            model.addAttribute("formAction", "/my/courses");
            return "course-form";
        }
        courseService.createDraft(form, authentication.getName());
        return "redirect:/my/courses";
    }

    @Audited
    @GetMapping("/{courseId}/edit")
    public String edit(@PathVariable Long courseId, Authentication authentication, Model model) {
        model.addAttribute("courseForm", courseService.getMyCourseForm(courseId, authentication.getName()));
        model.addAttribute("materials", materialService.findMyPublishedForSelection(authentication.getName()));
        model.addAttribute("formAction", "/my/courses/" + courseId);
        return "course-form";
    }

    @Audited
    @PostMapping("/{courseId}")
    public String update(@PathVariable Long courseId,
                         @Valid @ModelAttribute("courseForm") CourseFormDTO form,
                         BindingResult bindingResult, Authentication authentication, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("materials", materialService.findMyPublishedForSelection(authentication.getName()));
            model.addAttribute("formAction", "/my/courses/" + courseId);
            return "course-form";
        }
        courseService.updateDraft(courseId, form, authentication.getName());
        return "redirect:/my/courses";
    }

    @Audited
    @PostMapping("/{courseId}/submit")
    public String submit(@PathVariable Long courseId, Authentication authentication) {
        courseService.submitForReview(courseId, authentication.getName());
        return "redirect:/my/courses";
    }
}
