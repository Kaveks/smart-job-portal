package com.kaveks.commonlib.dto.job;

import com.kaveks.commonlib.domain.job.ExperienceLevel;
import com.kaveks.commonlib.domain.job.JobCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.Set;

public record JobRequest(

        @NotNull
        Long companyId,

        @NotBlank
        @Size(max = 200)
        String title,

        @NotBlank
        String description,

        String requirements,

        String responsibilities,

        @NotNull
        @Valid
        JobLocationRequest location,

        String benefits,

        @NotNull
        JobCategory category,

        @Valid
        SalaryRangeRequest salaryRange,

        @NotNull
        ExperienceLevel experienceLevel,

        @NotNull
        @Min(1)
        @Max(1000)
        Integer openings,

        @Future
        LocalDate deadline,

        @NotEmpty
        Set<@Valid JobTagDto> tags,

        @NotEmpty
        Set<@Valid JobSkillDto> skills

) {}