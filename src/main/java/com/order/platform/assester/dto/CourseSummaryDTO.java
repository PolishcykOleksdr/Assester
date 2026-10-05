package com.order.platform.assester.dto;

import com.order.platform.assester.enums.CourseAccessType;
import com.order.platform.assester.enums.CourseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CourseSummaryDTO(
        Long id,
        String title,
        String description,
        CourseAccessType accessType,
        BigDecimal price,
        String currency,
        CourseStatus status,
        String moderationComment,
        LocalDateTime updatedAt
) {}
