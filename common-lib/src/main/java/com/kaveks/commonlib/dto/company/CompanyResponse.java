package com.kaveks.commonlib.dto.company;

import com.kaveks.commonlib.domain.company.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CompanyResponse {

    private Long id;

    private String name;

    private String slug;

    private String tagLine;

    private String description;

    private String website;

    private String logoUrl;

    private String coverImageUrl;

    private String email;

    private String phone;

    private LocalDate dateFounded;

    private CompanySize companySize;

    private CompanyType companyType;

    private IndustryType industryType;

    private CompanyStatus status;

    private Boolean verified;

    private String regNumber;

    private Long ownerId;

    private List<SocialLinksResponse> socialLinks;

    private Boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}