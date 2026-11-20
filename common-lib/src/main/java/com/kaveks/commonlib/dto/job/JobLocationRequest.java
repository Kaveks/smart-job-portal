package com.kaveks.commonlib.dto.job;

import com.kaveks.commonlib.domain.job.WorkMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record JobLocationRequest(

        @NotBlank
        @Size(max = 100)
        String country,

        @Size(max = 100)
        String county,

        @NotBlank
        @Size(max = 100)
        String city,

        @Size(max = 255)
        String address,

        @NotNull
        WorkMode workMode

) {}