package com.order.platform.assester.dto;

import com.order.platform.assester.enums.CourseAccessType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class CourseFormDTO {
    @NotBlank
    @Size(max = 120)
    private String title;

    @NotBlank
    @Size(max = 3000)
    private String description;

    @NotNull
    private CourseAccessType accessType = CourseAccessType.FREE;

    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    private BigDecimal price;
}
