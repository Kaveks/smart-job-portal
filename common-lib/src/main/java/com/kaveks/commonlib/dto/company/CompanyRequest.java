package com.kaveks.commonlib.dto.company;

import com.kaveks.commonlib.domain.company.CompanySize;
import com.kaveks.commonlib.domain.company.CompanyType;
import com.kaveks.commonlib.domain.company.IndustryType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CompanyRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 150)
    private String name;

    @Email(message = "Provide a valid email")
    private String email;

    private String slug;

    @Size(max = 200)
    private String tagLine;

    @Size(max = 500)
    private String description;

    @Pattern(
            regexp = "^https?://.+",
            message = "Provide a valid website URL"
    )
    private String website;

    private String logoUrl;

    private String coverImageUrl;

    private String phone;

    @PastOrPresent(message = "Founding date cannot be in the future")
    private LocalDate dateFounded;

    @NotNull(message = "Company size is required")
    private CompanySize companySize;

    @NotNull(message = "Company type is required")
    private CompanyType companyType;

    @NotNull(message = "Industry type is required")
    private IndustryType industryType;

    @NotBlank(message = "Registration number is required")
    private String regNumber;

    @Valid
    private List<SocialLinksRequest> socialLinks;
}