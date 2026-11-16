package com.kaveks.commonlib.dto.company;

import com.kaveks.commonlib.domain.company.SocialPlatform;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SocialLinksResponse {
    private SocialPlatform platform;
    private String url;
}
