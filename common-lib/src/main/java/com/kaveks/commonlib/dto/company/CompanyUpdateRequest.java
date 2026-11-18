
package com.kaveks.commonlib.dto.company;

import com.kaveks.commonlib.domain.company.CompanySize;
import com.kaveks.commonlib.domain.company.CompanyType;
import com.kaveks.commonlib.domain.company.IndustryType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CompanyUpdateRequest {

    @Pattern(
            regexp = ".*\\S.*",
            message = "Company name cannot be blank"
    )
    @Size(max = 150, message = "Company name cannot exceed 150 characters")
    private String name;

    @Email(message = "Provide a valid email")
    @Pattern(
            regexp = ".*\\S.*",
            message = "Email cannot be blank"
    )
    private String email;

    @Pattern(regexp = ".*\\S.*", message = "Tagline cannot be blank")
    @Size(max = 200)
    private String tagLine;

    @Pattern(regexp = ".*\\S.*", message = "Description cannot be blank")
    @Size(max = 500)
    private String description;

    @Pattern(
            regexp = "^https?://.+",
            message = "Provide a valid website URL"
    )
    private String website;

    @Pattern(regexp = ".*\\S.*", message = "Logo URL cannot be blank")
    private String logoUrl;

    @Pattern(regexp = ".*\\S.*", message = "Cover image URL cannot be blank")
    private String coverImageUrl;

    @Pattern(regexp = ".*\\S.*", message = "Phone cannot be blank")
    private String phone;

    @PastOrPresent(message = "Founding date cannot be in the future")
    private LocalDate dateFounded;

    private CompanySize companySize;

    private CompanyType companyType;

    private IndustryType industryType;

    @Pattern(regexp = ".*\\S.*", message = "Registration number cannot be blank")
    private String regNumber;

    @Valid
    private List<SocialLinksRequest> socialLinks;
}
