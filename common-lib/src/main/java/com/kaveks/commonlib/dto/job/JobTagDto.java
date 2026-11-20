package com.kaveks.commonlib.dto.job;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JobTagDto(

        @NotBlank
        @Size(max = 50)
        String name

) {}