
package com.kaveks.jobservice.repository;

import com.kaveks.commonlib.domain.job.JobCategory;
import com.kaveks.commonlib.domain.job.JobStatus;
import com.kaveks.jobservice.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
@Repository
public interface JobRepository extends JpaRepository<Job, Long>,
        JpaSpecificationExecutor<Job> {

    Page<Job> findByStatusAndCategory(
            JobStatus status,
            JobCategory category,
            Pageable pageable
    );

    Page<Job> findByStatus(
            JobStatus status,
            Pageable pageable
    );

    Page<Job> findByCompanyId(Long companyId, Pageable pageable);

    Page<Job> findByCompanyIdAndStatus(
            Long companyId,
            JobStatus status,
            Pageable pageable
    );
}