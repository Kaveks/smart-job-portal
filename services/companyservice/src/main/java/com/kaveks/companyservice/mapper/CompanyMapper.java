
package com.kaveks.companyservice.mapper;

import com.kaveks.commonlib.dto.company.CompanyRequest;
import com.kaveks.commonlib.dto.company.CompanyResponse;
import com.kaveks.commonlib.dto.company.SocialLinksRequest;
import com.kaveks.commonlib.dto.company.SocialLinksResponse;
import com.kaveks.companyservice.model.Company;
import com.kaveks.companyservice.utils.SocialLinks;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class CompanyMapper {

    // Entity mapping
    public void toCompany(
            CompanyRequest request,
            Company company
    ) {

        if (StringUtils.hasText(request.getName())) {
            company.setName(request.getName());
        }

        if (StringUtils.hasText(request.getTagLine())) {
            company.setTagLine(request.getTagLine());
        }

        if (StringUtils.hasText(request.getDescription())) {
            company.setDescription(request.getDescription());
        }

        if (StringUtils.hasText(request.getWebsite())) {
            company.setWebsite(request.getWebsite());
        }

        if (StringUtils.hasText(request.getLogoUrl())) {
            company.setLogoUrl(request.getLogoUrl());
        }

        if (StringUtils.hasText(request.getCoverImageUrl())) {
            company.setCoverImageUrl(request.getCoverImageUrl());
        }

        if (request.getDateFounded() != null) {
            company.setDateFounded(request.getDateFounded());
        }

        if (StringUtils.hasText(request.getPhone())) {
            company.setPhone(request.getPhone());
        }

        if (StringUtils.hasText(request.getEmail())) {
            company.setEmail(request.getEmail());
        }

        if (request.getCompanySize() != null) {
            company.setCompanySize(request.getCompanySize());
        }

        if (request.getCompanyType() != null) {
            company.setCompanyType(request.getCompanyType());
        }

        if (request.getIndustryType() != null) {
            company.setIndustryType(request.getIndustryType());
        }

        if (StringUtils.hasText(request.getRegNumber())) {
            company.setRegNumber(request.getRegNumber());
        }

        if (request.getSocialLinks() != null) {
            company.setSocialLinks(toSocialLinks(request.getSocialLinks()));
        }
    }

    // Social links mapping
    public List<SocialLinks> toSocialLinks(
            List<SocialLinksRequest> requests
    ) {
        if (requests == null || requests.isEmpty()) {
            return new ArrayList<>();
        }

        List<SocialLinks> socialLinks = new ArrayList<>();

        for (SocialLinksRequest request : requests) {
            SocialLinks socialLink = new SocialLinks();
            socialLink.setPlatform(request.getPlatform());
            socialLink.setUrl(request.getUrl());

            socialLinks.add(socialLink);
        }

        return socialLinks;
    }

    // Response mapping
    public CompanyResponse toCompanyResponse(Company company) {

        CompanyResponse response = new CompanyResponse();

        response.setId(company.getId());
        response.setName(company.getName());
        response.setSlug(company.getSlug());
        response.setTagLine(company.getTagLine());
        response.setDescription(company.getDescription());
        response.setWebsite(company.getWebsite());
        response.setLogoUrl(company.getLogoUrl());
        response.setCoverImageUrl(company.getCoverImageUrl());
        response.setEmail(company.getEmail());
        response.setPhone(company.getPhone());
        response.setDateFounded(company.getDateFounded());
        response.setCompanySize(company.getCompanySize());
        response.setCompanyType(company.getCompanyType());
        response.setIndustryType(company.getIndustryType());
        response.setStatus(company.getStatus());
        response.setVerified(company.getVerified());
        response.setRegNumber(company.getRegNumber());
        response.setOwnerId(company.getOwnerId());
        response.setActive(company.getActive());
        response.setCreatedAt(company.getCreatedAt());
        response.setUpdatedAt(company.getUpdatedAt());

        List<SocialLinksResponse> socialLinks =
                company.getSocialLinks() == null
                        ? Collections.emptyList()
                        : company.getSocialLinks()
                        .stream()
                        .map(link -> SocialLinksResponse.builder()
                                .platform(link.getPlatform())
                                .url(link.getUrl())
                                .build())
                        .toList();

        response.setSocialLinks(new ArrayList<>(socialLinks));

        return response;
    }
}
