package com.kaveks.companyservice.service.impl;

import com.kaveks.commonlib.domain.company.CompanyStatus;
import com.kaveks.commonlib.domain.company.CompanyType;
import com.kaveks.commonlib.domain.company.IndustryType;
import com.kaveks.commonlib.dto.company.CompanyRequest;
import com.kaveks.commonlib.dto.company.CompanyResponse;
import com.kaveks.commonlib.dto.company.CompanyUpdateRequest;
import com.kaveks.companyservice.mapper.CompanyMapper;
import com.kaveks.companyservice.model.Company;
import com.kaveks.companyservice.repository.CompanyRepository;
import com.kaveks.companyservice.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;


    @Override
    @Transactional
    public CompanyResponse createCompany(Long ownerId, CompanyRequest request) {
        if(ownerId ==null){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Company owner is required"
            );
        }
        if(companyRepository.existsByOwnerId(ownerId)){
            if(companyRepository.existsByName(request.getName()) || companyRepository.existsByRegNumber(request.getRegNumber())){
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "You already have a company ( " +request.getName() + ", " + request.getRegNumber()+ " registered.)"
                );
            }
        }

        Company company = new Company();
        companyMapper.toCompany(request,company);

        company.setOwnerId(ownerId);
        company.setSlug(generateUniqueSlug(request.getName()));
        company.setStatus(CompanyStatus.PENDING);
        company.setVerified(false);
        company.setActive(true);

        Company savedCompany = companyRepository.save(company);

        return companyMapper.toCompanyResponse(savedCompany);
    }

    @Override
    @Transactional
    public CompanyResponse updateCompany(
            Long id,
            Long ownerId,
            CompanyUpdateRequest request
    ) {
        Company company = findCompanyById(id);

        validateOwnership(company, ownerId);

        if (!Boolean.TRUE.equals(company.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Company not found"
            );
        }

        boolean nameChanged = request.getName() != null
                && !company.getName().equals(request.getName());
        if( nameChanged && companyRepository.existsByName(request.getName())){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A company named:"+ request.getName() + " exists!"
            );
        }

        if (request.getRegNumber() != null) {
            validateRegistrationNumber(request.getRegNumber(), id);
        }

        companyMapper.updateCompany(request, company);

        if (nameChanged) {
            company.setSlug(generateUniqueSlug(company.getName()));
        }

        Company updatedCompany = companyRepository.save(company);

        return companyMapper.toCompanyResponse(updatedCompany);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyResponse getCompanyById(Long id) {

        Company company = findCompanyById(id);

        if (!Boolean.TRUE.equals(company.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Company not found"
            );
        }

        return companyMapper.toCompanyResponse(company);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyResponse getCompanyByIdInternal(Long id) {
        Company company =findCompanyById(id);
        return companyMapper.toCompanyResponse(company);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyResponse> getMyCompanies(Long ownerId) {
        if (ownerId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Company owner is required"
            );
        }
        return companyRepository.findAllByOwnerId(ownerId)
                .stream()
                .filter(company ->Boolean.TRUE.equals(company.getActive()))
                .map(companyMapper::toCompanyResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompanyResponse> companyList(
            CompanyType companyType,
            IndustryType industryType,
            CompanyStatus companyStatus
    ) {
        return companyRepository.findListedCompanies(
                        companyType,
                        industryType,
                        companyStatus
                )
                .stream()
                .map(companyMapper::toCompanyResponse)
                .toList();
    }
    // internal use

    @Override
    @Transactional(readOnly = true)
    public void verifyCompanyOwnership(Long companyId, Long ownerId) {

        if (ownerId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Company owner is required"
            );
        }

        Company company = findCompanyById(companyId);

        if (!Boolean.TRUE.equals(company.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Company not found"
            );
        }

        validateOwnership(company, ownerId);
    }

    @Override
    @Transactional(readOnly = true)
    public void verifyCompanyEligibility(Long companyId) {
        Company company = findCompanyById(companyId);

        if (!Boolean.TRUE.equals(company.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Company not found"
            );
        }

        if (!Boolean.TRUE.equals(company.getVerified())
                || company.getStatus() != CompanyStatus.ACTIVE) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Company is not eligible to publish jobs"
            );
        }
    }



    @Override
    public String verifyCompany(Long id) {
        Company company = findCompanyById(id);

        if (!Boolean.TRUE.equals(company.getActive())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Company not found"
            );
        }

        company.setVerified(true);
        company.setStatus(CompanyStatus.ACTIVE);

        companyRepository.save(company);

        return company.getName() + " successfully verified.";
    }

    @Override
    public void deleteCompany(Long id,Long ownerId) {
        Company company = findCompanyById(id);
        validateOwnership(company,ownerId);
        company.setActive(false);

        companyRepository.save(company);

    }

    @Override
    public String deactivateCompany(Long id) {
        Company company = findCompanyById(id);

        company.setStatus(CompanyStatus.INACTIVE);

        companyRepository.save(company);

        return "Company deactivated successfully";
    }

    @Override
    public String suspendCompany(Long id) {
        Company company = findCompanyById(id);

        company.setStatus(CompanyStatus.SUSPENDED);

        companyRepository.save(company);

        return "Company suspended successfully";
    }

    // support methods

    // Registration number validation
    private void validateRegistrationNumber(
            String regNumber,
            Long companyId
    ) {

        companyRepository.findByRegNumber(regNumber)
                .filter(existing -> !existing.getId().equals(companyId))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Company registration number already exists"
                    );
                });
    }
    // Find company
    private Company findCompanyById(Long id) {

        return companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Company not found"
                ));
    }

    private String generateUniqueSlug(String companyName) {

        String baseSlug = slugify(companyName);

        if (!StringUtils.hasText(baseSlug)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A valid company name is required to generate a slug"
            );
        }

        String slug = baseSlug;
        int suffix = 1;

        while (companyRepository.existsBySlug(slug)) {

            String suffixText = "-" + suffix++;

            int maxBaseLength = 180 - suffixText.length();

            slug = baseSlug.substring(
                    0,
                    Math.min(baseSlug.length(), maxBaseLength)
            ) + suffixText;
        }

        return slug;
    }
    // Normalize slug
    private String slugify(String value) {

        if (!StringUtils.hasText(value)) {
            return "";
        }

        return value.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }

    // Ownership validation
    private void validateOwnership(Company company, Long ownerId) {

        if (!Objects.equals(company.getOwnerId(), ownerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can only manage your own company"
            );
        }
    }


}
