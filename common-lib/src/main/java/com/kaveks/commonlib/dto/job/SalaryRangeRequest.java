package com.kaveks.commonlib.dto.job;

import com.kaveks.commonlib.domain.job.SalaryPeriod;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record SalaryRangeRequest(

        @DecimalMin("0.0")
        BigDecimal minAmount,

        @DecimalMin("0.0")
        BigDecimal maxAmount,

        @NotBlank
        @Size(min = 3, max = 3)
        @Pattern(regexp = "[A-Z]{3}")
        String currency,

        @NotNull
        SalaryPeriod period

) {}