package com.kaveks.jobservice.controller;

import com.kaveks.commonlib.domain.job.ExperienceLevel;
import com.kaveks.commonlib.domain.job.JobCategory;
import com.kaveks.commonlib.domain.job.JobStatus;
import com.kaveks.commonlib.domain.job.WorkMode;
import com.kaveks.commonlib.domain.user.UserRole;
import com.kaveks.commonlib.dto.job.JobRequest;
import com.kaveks.commonlib.dto.job.JobResponse;
import com.kaveks.commonlib.dto.job.JobStatusUpdateRequest;
import com.kaveks.commonlib.dto.job.JobUpdateRequest;
import com.kaveks.commonweb.security.annotation.AllowedRoles;
import com.kaveks.jobservice.service.JobService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;
    //PUBLIC ENDPOINTS

    @GetMapping
    public ResponseEntity<Page<JobResponse>> listJobs(
            @RequestParam(required = false) JobCategory category,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                jobService.listJobs(category, pageable)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<Page<JobResponse>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) JobCategory category,
            @RequestParam(required = false) ExperienceLevel experienceLevel,
            @RequestParam(required = false) WorkMode workMode,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String county,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) BigDecimal minSalary,
            @RequestParam(required = false) BigDecimal maxSalary,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                jobService.searchJobs(
                        keyword,
                        category,
                        experienceLevel,
                        workMode,
                        city,
                        county,
                        country,
                        minSalary,
                        maxSalary,
                        pageable
                )
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<JobResponse> getJobById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<Page<JobResponse>> getJobsByCompany(
            @PathVariable Long companyId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                jobService.getJobsByCompany(
                        companyId,
                        extractAccessToken(authorization),
                        pageable
                )
        );
    }

   // EMPLOYER ENDPOINTS

    @PostMapping
    @AllowedRoles(UserRole.ROLE_EMPLOYER)
    public ResponseEntity<JobResponse> createJob(
            @Valid @RequestBody JobRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        JobResponse response = jobService.createJob(
                extractAccessToken(authorization),
                request
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/company/{companyId}/employer")
    @AllowedRoles(UserRole.ROLE_EMPLOYER)
    public ResponseEntity<Page<JobResponse>> getEmployerJobsByCompany(
            @PathVariable Long companyId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                jobService.getEmployerJobsByCompany(
                        companyId,
                        extractAccessToken(authorization),
                        pageable
                )
        );
    }

    @PatchMapping("/{id}")
    @AllowedRoles(UserRole.ROLE_EMPLOYER)
    public ResponseEntity<JobResponse> updateJob(
            @PathVariable Long id,
            @Valid @RequestBody JobUpdateRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        return ResponseEntity.ok(
                jobService.updateJob(
                        id,
                        extractAccessToken(authorization),
                        request
                )
        );
    }

    @PatchMapping("/{id}/status")
    @AllowedRoles(UserRole.ROLE_EMPLOYER)
    public ResponseEntity<JobResponse> updateJobStatus(
            @PathVariable Long id,
            @Valid @RequestBody JobStatusUpdateRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        return ResponseEntity.ok(
                jobService.updateJobStatus(
                        id,
                        extractAccessToken(authorization),
                        request.status()
                )
        );
    }

    @DeleteMapping("/{id}")
    @AllowedRoles(UserRole.ROLE_EMPLOYER)
    public ResponseEntity<String> deleteJob(
            @PathVariable Long id,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    ) {
        jobService.deleteJob(
                id,
                extractAccessToken(authorization)
        );

        return ResponseEntity.noContent().build();
    }

    // HELPERS

    private String extractAccessToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Bearer token is required"
            );
        }

        return authorization.substring(7);
    }

}
