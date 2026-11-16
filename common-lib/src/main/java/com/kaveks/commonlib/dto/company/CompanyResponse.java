package com.kaveks.commonlib.dto;

import com.kaveks.commonlib.domain.CompanySize;
import com.kaveks.commonlib.domain.CompanyStatus;
import com.kaveks.commonlib.domain.CompanyType;
import com.kaveks.commonlib.domain.IndustryType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
public class CompanyResponse {

    private String name;
    private String slug;
    private String tagLine;
    private String description;
    private String website;
    private String logoUrl;
    private String coverImageUrl;
    private Date dateFounded;

    private CompanySize companySize;

    private CompanyType companyType;

    private IndustryType industryType;

    private CompanyStatus status;
    private String regNumber;
    private Long ownerId;
    private List<SocialLinksResponse> socialLinks;

    private Boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


}
