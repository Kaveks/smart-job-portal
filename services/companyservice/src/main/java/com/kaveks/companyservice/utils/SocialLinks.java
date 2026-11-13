package com.kaveks.companyservice.utils;

import com.kaveks.commonlib.domain.SocialPlatform;
import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SocialLinks {
    private SocialPlatform platform;
    private String url;
}
