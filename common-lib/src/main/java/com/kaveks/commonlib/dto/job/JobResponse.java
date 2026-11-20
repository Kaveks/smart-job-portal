package com.kaveks.commonlib.dto.job;

import com.kaveks.commonlib.domain.job.ExperienceLevel;
import com.kaveks.commonlib.domain.job.JobCategory;
import com.kaveks.commonlib.domain.job.JobStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public record JobResponse(

        Long id,
        String title,
        String description,
        String requirements,
        String responsibilities,
        Long companyId,
        JobLocationResponse location,
        String benefits,
        JobCategory category,
        SalaryRangeResponse salaryRange,
        Set<JobTagDto> tags,
        Set<JobSkillDto> skills,
        ExperienceLevel experienceLevel,
        JobStatus status,
        Integer openings,
        LocalDate deadline,
        LocalDateTime expiredAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {}