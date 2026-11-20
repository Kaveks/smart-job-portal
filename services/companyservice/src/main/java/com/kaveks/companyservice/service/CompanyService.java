package com.kaveks.companyservice.service;

import com.kaveks.commonlib.domain.company.CompanyStatus;
import com.kaveks.commonlib.domain.company.CompanyType;
import com.kaveks.commonlib.domain.company.IndustryType;
import com.kaveks.commonlib.dto.company.CompanyRequest;
import com.kaveks.commonlib.dto.company.CompanyResponse;
import com.kaveks.commonlib.dto.company.CompanyUpdateRequest;

import java.util.List;

public interface CompanyService {

    CompanyResponse createCompany(Long ownerId, CompanyRequest request);

    CompanyResponse getCompanyById(Long id);

    // Used for internal service-to-service communication
    CompanyResponse getCompanyByIdInternal(Long id);

    List<CompanyResponse> getMyCompanies(Long ownerId);;

    List<CompanyResponse> companyList(
            CompanyType companyType,
            IndustryType industryType,
            CompanyStatus companyStatus
    );

    CompanyResponse updateCompany(
            Long id,
            Long ownerId,
            CompanyUpdateRequest request
    );

    String verifyCompany(Long id);
    void verifyCompanyEligibility(Long companyId);

    void deleteCompany(Long id,Long ownerId);

    String deactivateCompany(Long id);

    String suspendCompany(Long id);
    void verifyCompanyOwnership(Long companyId, Long ownerId);
}