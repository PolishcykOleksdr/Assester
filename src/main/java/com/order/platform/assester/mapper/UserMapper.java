package com.order.platform.assester.mapper;

import com.order.platform.assester.dto.RegisterUserDTO;
import com.order.platform.assester.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Service;

/**
 * author: user,
 * date: 28.09.2026
 */

@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface UserMapper {
    User toEntity(RegisterUserDTO registerUserDTO);
}