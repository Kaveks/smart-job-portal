package com.kaveks.commonlib.dto;

import com.kaveks.commonlib.domain.CompanySize;
import com.kaveks.commonlib.domain.CompanyStatus;
import com.kaveks.commonlib.domain.CompanyType;
import com.kaveks.commonlib.domain.IndustryType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CompanyRequest {
    @NotBlank(message = "name is required")
    private String name;
    @Email(message = "provide a valid email")
    private String email;
    private String slug;
    private String tagLine;
    private String description;
    @Pattern(regexp = "^(https?://).*",message = "provide a valid website url")
    private String website;
    private String logoUrl;
    private String coverImageUrl;
    private String phone;
    private Date dateFounded;
    @NotBlank(message = "companySize is required")
    private CompanySize companySize;

    @NotBlank(message = "companyType is required")
    private CompanyType companyType;

    @NotBlank(message = "industryType is required")
    private IndustryType industryType;

    @NotBlank(message = "regNumber is required")
    private String regNumber;
    private Long ownerId;
    private List<SocialLinksResponse> socialLinks;


}
