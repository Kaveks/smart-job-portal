
package com.kaveks.jobservice.repository;

import com.kaveks.commonlib.domain.job.JobCategory;
import com.kaveks.commonlib.domain.job.JobStatus;
import com.kaveks.jobservice.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {

    @Query("""
    SELECT j FROM Job j
    WHERE (
        :keyword = ''
        OR LOWER(j.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(j.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(j.requirements) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(j.responsibilities) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR EXISTS (
            SELECT s FROM j.skills s
            WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
        OR EXISTS (
            SELECT t FROM j.tags t
            WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
        )
    )
    AND (:category IS NULL OR j.category = :category)
    AND (:status IS NULL OR j.status = :status)
    """)
    Page<Job> searchJobs(
            @Param("keyword") String keyword,
            @Param("category") JobCategory category,
            @Param("status") JobStatus status,
            Pageable pageable
    );


    Page<Job> findByCompanyId(Long companyId, Pageable pageable);
    Page<Job> findByCompanyIdAndStatus(
            Long companyId,
            JobStatus status,
            Pageable pageable
    );
}
