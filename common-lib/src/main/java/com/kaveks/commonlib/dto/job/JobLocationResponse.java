package com.kaveks.commonlib.dto.job;

import com.kaveks.commonlib.domain.job.WorkMode;

public record JobLocationResponse(
        String country,
        String county,
        String city,
        String address,
        WorkMode workMode
) {}