package com.order.platform.assester.controllers;

import com.order.platform.assester.logging.annotation.Audited;
import com.order.platform.assester.dto.CourseSummaryDTO;
import com.order.platform.assester.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * author: user,
 * date: 02.10.2026
 */

@Controller
@RequestMapping("/catalog")
@RequiredArgsConstructor
public class CatalogController {
    private final CourseService courseService;

    @Audited
    @GetMapping
    public String getCatalogPage(@RequestParam(required = false) String code,
                                 @RequestParam(required = false) String query,
                                 Model model) {
        model.addAttribute("searchCode", code == null ? "" : code.trim());
        model.addAttribute("searchQuery", query == null ? "" : query.trim());

        CourseSummaryDTO searchedCourse = code == null || code.isBlank() ? null : courseService.findPublishedCourseByCode(code);
        model.addAttribute("searchedCourse", searchedCourse);

        List<CourseSummaryDTO> displayCourses;
        if (code != null && !code.isBlank()) {
            displayCourses = searchedCourse == null ? List.of() : List.of(searchedCourse);
        } else if (query != null && !query.isBlank()) {
            displayCourses = courseService.searchPublishedCourses(query);
        } else {
            displayCourses = courseService.findPublishedCourses();
        }
        model.addAttribute("displayCourses", displayCourses);
        return "catalog";
    }
}
