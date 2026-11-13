package com.kaveks.companyservice.service;

import com.kaveks.commonlib.domain.CompanyStatus;
import com.kaveks.commonlib.domain.CompanyType;
import com.kaveks.commonlib.domain.IndustryType;
import com.kaveks.commonlib.dto.CompanyRequest;
import com.kaveks.commonlib.dto.CompanyResponse;

import java.util.List;

public interface CompanyService {
    CompanyResponse createCompany(Long ownerId, CompanyRequest request);
    CompanyResponse getCompanyById(Long id);
    CompanyResponse getCompanyEntityById(Long id); // fo interservice call
    CompanyResponse getMyCompany(Long ownerId);
    List<CompanyResponse> companyList(
            CompanyType companyType,
            IndustryType industryType,
            CompanyStatus companyStatus
    );
    CompanyResponse updateCompany(Long id,Long ownerId,CompanyRequest req);
    String verifyCompany(Long id);
    void deleteCompany(Long id);
    String deactivateCompany(Long id);
    String suspendCompany(Long id);
}
