package com.order.platform.assester.mapper;

import com.order.platform.assester.dto.CourseFormDTO;
import com.order.platform.assester.dto.CourseSummaryDTO;
import com.order.platform.assester.entities.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * author: user,
 * date: 24.10.2026
 */

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = {MaterialMapper.class}
)
public interface CourseMapper {
    CourseSummaryDTO toSummary(Course course);

    @Mapping(source = "courseMaterials", target = "materialIds")
    CourseFormDTO toForm(Course course);
}
