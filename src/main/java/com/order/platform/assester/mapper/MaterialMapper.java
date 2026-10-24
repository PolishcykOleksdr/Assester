package com.order.platform.assester.mapper;

import com.order.platform.assester.dto.CourseSummaryDTO;
import com.order.platform.assester.dto.MaterialSummaryDTO;
import com.order.platform.assester.entities.Course;
import com.order.platform.assester.entities.Material;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * author: user,
 * date: 24.10.2026
 */

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface MaterialMapper {
    MaterialSummaryDTO toSummary(Material material);
    Long toId(Material material);
}
