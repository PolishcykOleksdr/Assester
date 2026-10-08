package com.order.platform.assester.dto;

import java.time.LocalDateTime;

public record MaterialSummaryDTO(Long id, String materialCode, String title, String description,
                                String authorName, int fileCount, LocalDateTime createdAt) {}
