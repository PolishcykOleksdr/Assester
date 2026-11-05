package com.order.platform.assester.mapper;

import com.order.platform.assester.dto.CourseMaterialSummaryDTO;
import com.order.platform.assester.entities.CourseMaterial;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * author: user,
 * date: 26.10.2026
 */

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface CourseMaterialMapper {
    @Mapping(target = "title", source = "material.title")
    @Mapping(target = "materialCode", source = "material.materialCode")
    @Mapping(target = "materialId", source = "material.id")
    CourseMaterialSummaryDTO toSummary(CourseMaterial courseMaterial);

    List<Long> toIds(List<CourseMaterial> courseMaterials);

    default Long toId(CourseMaterial courseMaterial) {
        return courseMaterial == null || courseMaterial.getMaterial() == null
                ? null
                : courseMaterial.getMaterial().getId();
    }
}
