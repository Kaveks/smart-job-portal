package com.kaveks.jobservice.model;

import com.kaveks.commonlib.domain.job.ExperienceLevel;
import com.kaveks.commonlib.domain.job.JobCategory;
import com.kaveks.commonlib.domain.job.JobStatus;
import com.kaveks.jobservice.utils.JobLocation;
import com.kaveks.jobservice.utils.JobSkill;
import com.kaveks.jobservice.utils.JobTag;
import com.kaveks.jobservice.utils.SalaryRange;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "jobs",
        indexes = {
                @Index(
                        name = "idx_jobs_company_id",
                        columnList = "company_id"
                ),
                @Index(
                        name = "idx_jobs_category",
                        columnList = "category"
                ),
                @Index(
                        name = "idx_jobs_location",
                        columnList = "country, city"
                ),
                @Index(
                        name = "idx_jobs_work_mode",
                        columnList = "work_mode"
                ),
                @Index(
                        name = "idx_jobs_salary_currency_min",
                        columnList = "salary_currency, salary_min"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Column(columnDefinition = "TEXT")
    private String responsibilities;

    @Column(nullable = false)
    private Long companyId;

    @Embedded
    private JobLocation location;

    @Column(columnDefinition = "TEXT")
    private String benefits;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobCategory category;

    @Embedded
    private SalaryRange salaryRange;

    @ElementCollection
    @CollectionTable(
            name = "job_tags",
            joinColumns = @JoinColumn(name = "job_id"),
            indexes = {
                    @Index(name = "idx_job_tags_job_id", columnList = "job_id"),
                    @Index(name = "idx_job_tags_tag", columnList = "tag")
            },
            uniqueConstraints = {
                    @UniqueConstraint(
                            name = "uk_job_tags_job_tag",
                            columnNames = {"job_id", "tag"}
                    )
            }
    )
    @AttributeOverride(
            name = "name",
            column = @Column(name = "tag", nullable = false, length = 50)
    )
    @Builder.Default
    private Set<JobTag> tags = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "job_skills",
            joinColumns = @JoinColumn(name = "job_id"),
            indexes = {
                    @Index(name = "idx_job_skills_job_id", columnList = "job_id"),
                    @Index(name = "idx_job_skills_skill", columnList = "skill")
            },
            uniqueConstraints = {
                    @UniqueConstraint(
                            name = "uk_job_skills_job_skill",
                            columnNames = {"job_id", "skill"}
                    )
            }
    )
    @AttributeOverride(
            name = "name",
            column = @Column(name = "skill", nullable = false, length = 100)
    )
    @Builder.Default
    private Set<JobSkill> skills = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExperienceLevel experienceLevel;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private JobStatus status = JobStatus.DRAFT;

    @Builder.Default
    @Column(nullable = false)
    private Integer openings = 1;
    private LocalDate deadline;
    private LocalDateTime expiredAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;
    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}