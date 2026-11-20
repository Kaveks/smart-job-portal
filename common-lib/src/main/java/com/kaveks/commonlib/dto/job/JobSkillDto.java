package com.kaveks.commonlib.dto.job;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JobSkillDto(

        @NotBlank
        @Size(max = 100)
        String name

) {}