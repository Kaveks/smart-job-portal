package com.kaveks.companyservice.repository;

import com.kaveks.commonlib.domain.company.CompanyStatus;
import com.kaveks.commonlib.domain.company.CompanyType;
import com.kaveks.commonlib.domain.company.IndustryType;
import com.kaveks.companyservice.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

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

    List<Company> findByCompanyTypeAndIndustryTypeAndStatusAndActiveTrue(
            CompanyType companyType,
            IndustryType industryType,
            CompanyStatus status
    );

    List<Company> findByActiveTrue();

    List<Company> findByStatusAndActiveTrue(CompanyStatus status);
}