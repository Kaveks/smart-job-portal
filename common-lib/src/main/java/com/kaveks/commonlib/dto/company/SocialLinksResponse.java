package com.kaveks.commonlib.dto;

import com.kaveks.commonlib.domain.SocialPlatform;
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
