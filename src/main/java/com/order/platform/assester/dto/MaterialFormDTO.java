package com.order.platform.assester.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialFormDTO {
    @NotBlank
    @Size(max = 120)
    private String title;

    @NotBlank
    @Size(max = 3000)
    private String description;
}
