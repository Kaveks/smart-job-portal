package com.kaveks.commonlib.dto.job;

import com.kaveks.commonlib.domain.job.WorkMode;
import jakarta.validation.constraints.Pattern;

public record JobLocationUpdateRequest(

        @Pattern(regexp = ".*\\S.*", message = "Country cannot be blank")
        String country,

        @Pattern(regexp = ".*\\S.*", message = "County cannot be blank")
        String county,

        @Pattern(regexp = ".*\\S.*", message = "City cannot be blank")
        String city,

        @Pattern(regexp = ".*\\S.*", message = "Address cannot be blank")
        String address,

        WorkMode workMode

) {}