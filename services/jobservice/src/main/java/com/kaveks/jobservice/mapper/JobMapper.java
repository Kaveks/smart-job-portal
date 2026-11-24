
package com.kaveks.jobservice.mapper;



import com.kaveks.commonlib.dto.job.*;
import com.kaveks.jobservice.model.Job;
import com.kaveks.jobservice.utils.JobLocation;
import com.kaveks.jobservice.utils.JobSkill;
import com.kaveks.jobservice.utils.JobTag;
import com.kaveks.jobservice.utils.SalaryRange;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class JobMapper {

    public Job toJobEntity(JobRequest request) {
        Job job = new Job();

        job.setCompanyId(request.companyId());
        job.setTitle(request.title());
        job.setDescription(request.description());
        job.setRequirements(request.requirements());
        job.setResponsibilities(request.responsibilities());
        job.setLocation(toLocation(request.location()));
        job.setBenefits(request.benefits());
        job.setCategory(request.category());
        job.setSalaryRange(toSalaryRange(request.salaryRange()));
        job.setExperienceLevel(request.experienceLevel());
        job.setOpenings(request.openings());
        job.setDeadline(request.deadline());
        job.setTags(toTags(request.tags()));
        job.setSkills(toSkills(request.skills()));

        return job;
    }

    public void updateJob(Job job, JobUpdateRequest request) {

        if (request.title() != null) {
            job.setTitle(request.title());
        }

        if (request.description() != null) {
            job.setDescription(request.description());
        }

        if (request.requirements() != null) {
            job.setRequirements(request.requirements());
        }

        if (request.responsibilities() != null) {
            job.setResponsibilities(request.responsibilities());
        }

        if (request.location() != null) {
            job.setLocation(mergeLocation(job.getLocation(), request.location()));
        }

        if (request.benefits() != null) {
            job.setBenefits(request.benefits());
        }

        if (request.category() != null) {
            job.setCategory(request.category());
        }

        if (request.salaryRange() != null) {
            job.setSalaryRange(mergeSalaryRange(
                    job.getSalaryRange(),
                    request.salaryRange()
            ));
        }

        if (request.experienceLevel() != null) {
            job.setExperienceLevel(request.experienceLevel());
        }

        if (request.openings() != null) {
            job.setOpenings(request.openings());
        }

        if (request.deadline() != null) {
            job.setDeadline(request.deadline());
        }

        if (request.tags() != null) {
            job.setTags(toTags(request.tags()));
        }

        if (request.skills() != null) {
            job.setSkills(toSkills(request.skills()));
        }
    }
    private SalaryRange mergeSalaryRange(
            SalaryRange existing,
            SalaryRangeUpdateRequest request
    ) {
        return SalaryRange.builder()
                .minAmount(request.minAmount() != null
                        ? request.minAmount()
                        : existing == null ? null : existing.getMinAmount())
                .maxAmount(request.maxAmount() != null
                        ? request.maxAmount()
                        : existing == null ? null : existing.getMaxAmount())
                .currency(request.currency() != null
                        ? request.currency()
                        : existing == null ? null : existing.getCurrency())
                .period(request.period() != null
                        ? request.period()
                        : existing == null ? null : existing.getPeriod())
                .build();
    }

    private JobLocation mergeLocation(
            JobLocation existing,
            JobLocationUpdateRequest request
    ) {
        return JobLocation.builder()
                .country(request.country() != null
                        ? request.country()
                        : existing == null ? null : existing.getCountry())
                .county(request.county() != null
                        ? request.county()
                        : existing == null ? null : existing.getCounty())
                .city(request.city() != null
                        ? request.city()
                        : existing == null ? null : existing.getCity())
                .address(request.address() != null
                        ? request.address()
                        : existing == null ? null : existing.getAddress())
                .workMode(request.workMode() != null
                        ? request.workMode()
                        : existing == null ? null : existing.getWorkMode())
                .build();
    }
    public JobResponse toJobResponse(Job job) {
        return new JobResponse(
                job.getId(),
                job.getTitle(),
                job.getDescription(),
                job.getRequirements(),
                job.getResponsibilities(),
                job.getCompanyId(),
                toLocationResponse(job.getLocation()),
                job.getBenefits(),
                job.getCategory(),
                toSalaryRangeResponse(job.getSalaryRange()),
                toTagDtos(job.getTags()),
                toSkillDtos(job.getSkills()),
                job.getExperienceLevel(),
                job.getStatus(),
                job.getOpenings(),
                job.getDeadline(),
                job.getClosedAt(),
                job.getPublishedAt(),
                job.getExpiredAt(),
                job.getCreatedAt(),
                job.getUpdatedAt()
        );
    }

    private JobLocation toLocation(
            com.kaveks.commonlib.dto.job.JobLocationRequest request
    ) {
        if (request == null) {
            return null;
        }

        return JobLocation.builder()
                .country(request.country())
                .county(request.county())
                .city(request.city())
                .address(request.address())
                .workMode(request.workMode())
                .build();
    }

    private SalaryRange toSalaryRange(
            com.kaveks.commonlib.dto.job.SalaryRangeRequest request
    ) {
        if (request == null) {
            return null;
        }

        return SalaryRange.builder()
                .minAmount(request.minAmount())
                .maxAmount(request.maxAmount())
                .currency(request.currency())
                .period(request.period())
                .build();
    }

    private JobLocationResponse toLocationResponse(JobLocation location) {
        if (location == null) {
            return null;
        }

        return new JobLocationResponse(
                location.getCountry(),
                location.getCounty(),
                location.getCity(),
                location.getAddress(),
                location.getWorkMode()
        );
    }

    private SalaryRangeResponse toSalaryRangeResponse(SalaryRange salaryRange) {
        if (salaryRange == null) {
            return null;
        }

        return new SalaryRangeResponse(
                salaryRange.getMinAmount(),
                salaryRange.getMaxAmount(),
                salaryRange.getCurrency(),
                salaryRange.getPeriod()
        );
    }

    private Set<JobTag> toTags(Set<JobTagDto> tags) {
        if (tags == null) {
            return Set.of();
        }

        return tags.stream()
                .map(tag -> JobTag.builder()
                        .name(tag.name())
                        .build())
                .collect(Collectors.toSet());
    }

    private Set<JobSkill> toSkills(Set<JobSkillDto> skills) {
        if (skills == null) {
            return Set.of();
        }

        return skills.stream()
                .map(skill -> JobSkill.builder()
                        .name(skill.name())
                        .build())
                .collect(Collectors.toSet());
    }

    private Set<JobTagDto> toTagDtos(Set<JobTag> tags) {
        if (tags == null) {
            return Set.of();
        }

        return tags.stream()
                .map(tag -> new JobTagDto(tag.getName()))
                .collect(Collectors.toSet());
    }

    private Set<JobSkillDto> toSkillDtos(Set<JobSkill> skills) {
        if (skills == null) {
            return Set.of();
        }

        return skills.stream()
                .map(skill -> new JobSkillDto(skill.getName()))
                .collect(Collectors.toSet());
    }
}
