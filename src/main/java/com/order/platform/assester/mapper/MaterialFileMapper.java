package com.order.platform.assester.mapper;

import com.order.platform.assester.dto.MaterialFileDTO;
import com.order.platform.assester.entities.MaterialFile;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

/**
 * author: user,
 * date: 25.10.2026
 */

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface MaterialFileMapper {
    MaterialFileDTO toDTO(MaterialFile materialFile);
}