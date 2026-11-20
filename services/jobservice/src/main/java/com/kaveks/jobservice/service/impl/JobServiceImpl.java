package com.kaveks.jobservice.service.impl;

import com.kaveks.commonlib.domain.job.JobCategory;
import com.kaveks.commonlib.domain.job.JobStatus;
import com.kaveks.commonlib.dto.job.JobRequest;
import com.kaveks.commonlib.dto.job.JobResponse;
import com.kaveks.commonlib.dto.job.JobUpdateRequest;
import com.kaveks.jobservice.config.clients.CompanyServiceClient;
import com.kaveks.jobservice.mapper.JobMapper;
import com.kaveks.jobservice.model.Job;
import com.kaveks.jobservice.repository.JobRepository;
import com.kaveks.jobservice.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;


@Service
@RequiredArgsConstructor
@Transactional
public class JobServiceImpl implements JobService {
    private final JobRepository jobRepository;
    private final JobMapper jobMapper;
    private final CompanyServiceClient companyServiceClient;

    @Override
    @Transactional
    public JobResponse createJob(String accessToken, JobRequest request) {

        // Verify that the authenticated employer owns the company.
        companyServiceClient.verifyCompanyOwnership(
                request.companyId(),
                accessToken
        );

        // Verify that the company is eligible to publish jobs.
        companyServiceClient.verifyCompanyEligibility(request.companyId(),accessToken);

        // Map the request into a Job entity.
        Job job = jobMapper.toJobEntity(request);

        // New jobs start as DRAFT.
        job.setStatus(JobStatus.DRAFT);

        // Persist the job.
        Job savedJob = jobRepository.save(job);

        return jobMapper.toResponse(savedJob);
    }


    @Override
    @Transactional(readOnly = true)
    public JobResponse getJobById(Long id) {
        Job job = findJobById(id);

        if (job.getStatus() != JobStatus.OPEN) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Job not found"
            );
        }

        return jobMapper.toResponse(job);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobResponse> listJobs(
            JobCategory category,
            Pageable pageable
    ) {
        return jobRepository.searchJobs(
                        "",
                        category,
                        JobStatus.OPEN,
                        pageable
                )
                .map(jobMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobResponse> searchJobs(
            String keyword,
            JobCategory category,
            Pageable pageable
    ) {
        String searchTerm = (keyword == null || keyword.isBlank())
                ? null
                : keyword.trim();

        return jobRepository.searchJobs(
                        searchTerm,
                        category,
                        JobStatus.OPEN,
                        pageable
                )
                .map(jobMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobResponse> getJobsByCompany(
            Long companyId,
            String accessToken,
            Pageable pageable
    ) {
        companyServiceClient.verifyCompanyEligibility(companyId,accessToken);

        return jobRepository.findByCompanyIdAndStatus(
                        companyId,
                        JobStatus.OPEN,
                        pageable
                )
                .map(jobMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<JobResponse> getEmployerJobsByCompany(
            Long companyId,
            String accessToken,
            Pageable pageable
    ) {
        // Verify that the authenticated employer owns the company.
        companyServiceClient.verifyCompanyOwnership(
                companyId,
                accessToken
        );

        // Retrieve all jobs belonging to the company.
        return jobRepository.findByCompanyId(companyId, pageable)
                .map(jobMapper::toResponse);
    }


    @Override
    @Transactional
    public JobResponse updateJob(
            Long id,
            String accessToken,
            JobUpdateRequest request
    ) {
        // Find the existing job.
        Job job = findJobById(id);

        // Verify that the authenticated employer owns the company
        // associated with this job.
        companyServiceClient.verifyCompanyOwnership(
                job.getCompanyId(),
                accessToken
        );

        // Update editable job fields without changing companyId or status.
        jobMapper.updateJob(job, request);

        // Persist and return the updated job.
        Job updatedJob = jobRepository.save(job);

        return jobMapper.toResponse(updatedJob);
    }



    @Override
    @Transactional
    public void deleteJob(Long id, String accessToken) {
        // Find the existing job.
        Job job = findJobById(id);

        // Verify that the authenticated employer owns its company.
        companyServiceClient.verifyCompanyOwnership(
                job.getCompanyId(),
                accessToken
        );

        // Delete the job.
        jobRepository.delete(job);

    }

    @Override
    @Transactional
    public JobResponse updateJobStatus(
            Long id,
            String accessToken,
            JobStatus status
    ) {
        // Find the existing job.
        Job job = findJobById(id);

        // Verify that the authenticated employer owns its company.
        companyServiceClient.verifyCompanyOwnership(
                job.getCompanyId(),
                accessToken
        );

        // Validate the requested status transition.
        validateStatusTransition(job.getStatus(), status);

        // Prevent publishing or reopening jobs past their deadline.
        if (status == JobStatus.OPEN
                && job.getDeadline() != null
                && !job.getDeadline().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot publish or reopen a job whose deadline has passed"
            );
        }

        // Verify company eligibility before publishing or reopening.
        if (status == JobStatus.OPEN) {
            companyServiceClient.verifyCompanyEligibility(
                    job.getCompanyId(),
                    accessToken
            );
        }

        // Update only the job status.
        job.setStatus(status);

        // Persist and return the updated job.
        Job updatedJob = jobRepository.save(job);

        return jobMapper.toResponse(updatedJob);
    }




    // helper methods
    private Job findJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Job not found"
                ));
    }


    private void validateStatusTransition(
            JobStatus currentStatus,
            JobStatus newStatus
    ) {
        if (newStatus == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Job status is required"
            );
        }

        if (currentStatus == newStatus) {
            return;
        }

        boolean allowed = switch (currentStatus) {
            case DRAFT -> newStatus == JobStatus.OPEN;

            case OPEN ->
                    newStatus == JobStatus.CLOSED
                            || newStatus == JobStatus.FILLED;

            case CLOSED ->
                    newStatus == JobStatus.OPEN
                            || newStatus == JobStatus.FILLED;

            case FILLED, EXPIRED -> false;
        };

        if (!allowed) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid job status transition from "
                            + currentStatus + " to " + newStatus
            );
        }
    }


}
