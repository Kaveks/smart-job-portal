package com.kaveks.commonlib.dto.job;

import com.kaveks.commonlib.domain.job.ExperienceLevel;
import com.kaveks.commonlib.domain.job.JobCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.Set;

public record JobUpdateRequest(

        @Size(max = 200)
        @Pattern(regexp = ".*\\S.*", message = "Title cannot be blank")
        String title,

        @Pattern(regexp = ".*\\S.*", message = "Description cannot be blank")
        String description,

        @Pattern(regexp = ".*\\S.*", message = "Requirements cannot be blank")
        String requirements,

        @Pattern(regexp = ".*\\S.*", message = "Responsibilities cannot be blank")
        String responsibilities,

        @Valid
        JobLocationUpdateRequest location,

        @Pattern(regexp = ".*\\S.*", message = "Benefits cannot be blank")
        String benefits,

        JobCategory category,

        @Valid
        SalaryRangeUpdateRequest salaryRange,

        ExperienceLevel experienceLevel,

        @Min(1)
        @Max(1000)
        Integer openings,

        @Future
        LocalDate deadline,

        @Size(min = 1, message = "Tags cannot be empty")
        Set<@Valid JobTagDto> tags,

        @Size(min = 1, message = "Skills cannot be empty")
        Set<@Valid JobSkillDto> skills

) {}