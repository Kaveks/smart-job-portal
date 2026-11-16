package com.kaveks.commonlib.dto.company;

import com.kaveks.commonlib.domain.company.SocialPlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SocialLinksRequest {

    @NotNull(message = "Social platform is required")
    private SocialPlatform platform;

    @NotBlank(message = "Social link URL is required")
    @Pattern(
            regexp = "^https?://.+",
            message = "Provide a valid social link URL"
    )
    private String url;
}