package com.kaveks.companyservice.service;

import com.kaveks.commonlib.domain.company.CompanyStatus;
import com.kaveks.commonlib.domain.company.CompanyType;
import com.kaveks.commonlib.domain.company.IndustryType;
import com.kaveks.commonlib.dto.company.CompanyRequest;
import com.kaveks.commonlib.dto.company.CompanyResponse;

import java.util.List;

public interface CompanyService {

    CompanyResponse createCompany(Long ownerId, CompanyRequest request);

    CompanyResponse getCompanyById(Long id);

    // Used for internal service-to-service communication
    CompanyResponse getCompanyByIdInternal(Long id);

    CompanyResponse getMyCompany(Long ownerId);

    List<CompanyResponse> companyList(
            CompanyType companyType,
            IndustryType industryType,
            CompanyStatus companyStatus
    );

    CompanyResponse updateCompany(
            Long id,
            Long ownerId,
            CompanyRequest request
    );

    String verifyCompany(Long id);

    void deleteCompany(Long id);

    String deactivateCompany(Long id);

    String suspendCompany(Long id);
}