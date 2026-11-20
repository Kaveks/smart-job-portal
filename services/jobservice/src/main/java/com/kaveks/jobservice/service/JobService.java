package com.kaveks.jobservice.service;

import com.kaveks.commonlib.domain.job.JobCategory;
import com.kaveks.commonlib.domain.job.JobStatus;
import com.kaveks.commonlib.dto.job.JobRequest;
import com.kaveks.commonlib.dto.job.JobResponse;
import com.kaveks.commonlib.dto.job.JobUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JobService {

    JobResponse createJob(
            String accessToken,
            JobRequest request
    );

    JobResponse getJobById(Long id);
    Page<JobResponse> listJobs(
            JobCategory category,
            Pageable pageable
    );

    Page<JobResponse> searchJobs(
            String keyword,
            JobCategory category,
            Pageable pageable
    );

    Page<JobResponse> getJobsByCompany(
            Long companyId,
            String accessToken,
            Pageable pageable
    );
    Page<JobResponse> getEmployerJobsByCompany(
            Long companyId,
            String accessToken,
            Pageable pageable
    );

    JobResponse updateJob(
            Long id,
            String accessToken,
            JobUpdateRequest request
    );
    JobResponse updateJobStatus(
            Long id,
            String accessToken,
            JobStatus status
    );

    void deleteJob(
            Long id,
            String accessToken
    );

}