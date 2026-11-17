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

        Specification<Company> specification =
                (root, query, criteriaBuilder) ->
                        criteriaBuilder.and(
                                criteriaBuilder.isTrue(root.get("active")),
                                criteriaBuilder.isTrue(root.get("verified"))
                        );

        if (companyType != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("companyType"),
                                    companyType
                            )
            );
        }

        if (industryType != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("industryType"),
                                    industryType
                            )
            );
        }

        if (companyStatus != null) {
            specification = specification.and(
                    (root, query, criteriaBuilder) ->
                            criteriaBuilder.equal(
                                    root.get("status"),
                                    companyStatus
                            )
            );
        }

        return companyRepository.findAll(specification)
                .stream()
                .map(companyMapper::toCompanyResponse)
                .toList();
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
    public void deleteCompany(Long id) {
        Company company = findCompanyById(id);

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
    //validate slug
    private void validateSlug(String slug, Long companyId){
        companyRepository.findBySlug(slug)
                .filter(existing -> !existing.getId().equals(companyId))
                .ifPresent(existing -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Company slug already exists"
                    );
                });
    }
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

    // Ownership validation
    private void validateOwnership(Company company, Long ownerId) {

        if (!Objects.equals(company.getOwnerId(), ownerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to manage this company"
            );
        }
    }


}
