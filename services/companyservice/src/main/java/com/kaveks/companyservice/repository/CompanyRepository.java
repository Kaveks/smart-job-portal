package com.kaveks.companyservice.repository;

import com.kaveks.commonlib.domain.company.CompanyStatus;
import com.kaveks.commonlib.domain.company.CompanyType;
import com.kaveks.commonlib.domain.company.IndustryType;
import com.kaveks.companyservice.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends
        JpaRepository<Company, Long>,
        JpaSpecificationExecutor<Company> {

    Optional<Company> findBySlug(String slug);

    Optional<Company> findByRegNumber(String regNumber);

    List<Company> findAllByOwnerId(Long ownerId);

    boolean existsBySlug(String slug);

    boolean existsByRegNumber(String regNumber);
    boolean existsByName(String name);
    boolean existsByOwnerId(Long ownerId);

    @Query("""
        SELECT c
        FROM Company c
        WHERE c.active = true
          AND c.verified = true
          AND (:companyType IS NULL OR c.companyType = :companyType)
          AND (:industryType IS NULL OR c.industryType = :industryType)
          AND (:companyStatus IS NULL OR c.status = :companyStatus)
        """)
    List<Company> findListedCompanies(
            @Param("companyType") CompanyType companyType,
            @Param("industryType") IndustryType industryType,
            @Param("companyStatus") CompanyStatus companyStatus
    );

    List<Company> findByActiveTrue();

    List<Company> findByStatusAndActiveTrue(CompanyStatus status);
}