
package com.kaveks.jobservice.specification;

import com.kaveks.commonlib.domain.job.ExperienceLevel;
import com.kaveks.commonlib.domain.job.JobCategory;
import com.kaveks.commonlib.domain.job.JobStatus;
import com.kaveks.commonlib.domain.job.WorkMode;
import com.kaveks.jobservice.model.Job;
import com.kaveks.jobservice.utils.JobSkill;
import com.kaveks.jobservice.utils.JobTag;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class JobSpecification {

    private JobSpecification() {
    }

    public static Specification<Job> searchJobs(
            String keyword,
            JobCategory category,
            ExperienceLevel experienceLevel,
            WorkMode workMode,
            String city,
            String county,
            String country,
            BigDecimal minSalary,
            BigDecimal maxSalary
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Public search must only return OPEN jobs.
            predicates.add(
                    cb.equal(root.get("status"), JobStatus.OPEN)
            );

            // Keyword search
            if (keyword != null && !keyword.isBlank()) {

                String pattern = "%"
                        + keyword.trim().toLowerCase()
                        + "%";

                Predicate keywordPredicate = cb.or(
                        cb.like(cb.lower(root.get("title")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern),
                        cb.like(cb.lower(root.get("requirements")), pattern),
                        cb.like(cb.lower(root.get("responsibilities")), pattern),
                        skillMatches(root, query, cb, pattern),
                        tagMatches(root, query, cb, pattern)
                );

                predicates.add(keywordPredicate);
            }

            // Category
            if (category != null) {
                predicates.add(
                        cb.equal(root.get("category"), category)
                );
            }

            // Experience
            if (experienceLevel != null) {
                predicates.add(
                        cb.equal(root.get("experienceLevel"), experienceLevel)
                );
            }

            // Work mode
            if (workMode != null) {
                predicates.add(
                        cb.equal(
                                root.get("location").get("workMode"),
                                workMode
                        )
                );
            }

            // Location filters
            addLocationPredicate(predicates, root, cb, "city", city);
            addLocationPredicate(predicates, root, cb, "county", county);
            addLocationPredicate(predicates, root, cb, "country", country);

            // Salary filters
            addSalaryPredicates(
                    predicates,
                    root,
                    cb,
                    minSalary,
                    maxSalary
            );

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate skillMatches(
            Root<Job> root,
            CriteriaQuery<?> query,
            CriteriaBuilder cb,
            String pattern
    ) {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<Job> job = subquery.from(Job.class);

        Join<Job, JobSkill> skills = job.join("skills", JoinType.INNER);

        subquery.select(job.get("id"))
                .where(
                        cb.equal(job.get("id"), root.get("id")),
                        cb.like(cb.lower(skills.get("name")), pattern)
                );

        return cb.exists(subquery);
    }

    private static Predicate tagMatches(
            Root<Job> root,
            CriteriaQuery<?> query,
            CriteriaBuilder cb,
            String pattern
    ) {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<Job> job = subquery.from(Job.class);

        Join<Job, JobTag> tags = job.join("tags", JoinType.INNER);

        subquery.select(job.get("id"))
                .where(
                        cb.equal(job.get("id"), root.get("id")),
                        cb.like(cb.lower(tags.get("name")), pattern)
                );

        return cb.exists(subquery);
    }

    private static void addLocationPredicate(
            List<Predicate> predicates,
            Root<Job> root,
            CriteriaBuilder cb,
            String field,
            String value
    ) {
        if (value != null && !value.isBlank()) {
            Path<String> locationField = root.get("location").get(field);

            predicates.add(
                    cb.like(
                            cb.lower(locationField),
                            "%" + value.trim().toLowerCase() + "%"
                    )
            );
        }
    }

    private static void addSalaryPredicates(
            List<Predicate> predicates,
            Root<Job> root,
            CriteriaBuilder cb,
            BigDecimal minSalary,
            BigDecimal maxSalary
    ) {
        if (minSalary == null && maxSalary == null) {
            return;
        }

        Path<BigDecimal> jobMin = root
                .get("salaryRange")
                .get("minAmount");

        Path<BigDecimal> jobMax = root
                .get("salaryRange")
                .get("maxAmount");

        // Ignore jobs without salary amounts.
        predicates.add(
                cb.or(
                        cb.isNotNull(jobMin),
                        cb.isNotNull(jobMax)
                )
        );

        // Job maximum must be greater than or equal to requested minimum.
        if (minSalary != null) {
            predicates.add(
                    cb.or(
                            cb.isNull(jobMax),
                            cb.greaterThanOrEqualTo(jobMax, minSalary)
                    )
            );
        }

        // Job minimum must be less than or equal to requested maximum.
        if (maxSalary != null) {
            predicates.add(
                    cb.or(
                            cb.isNull(jobMin),
                            cb.lessThanOrEqualTo(jobMin, maxSalary)
                    )
            );
        }
    }
}
