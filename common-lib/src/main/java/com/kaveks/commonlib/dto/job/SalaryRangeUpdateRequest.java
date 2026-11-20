package com.kaveks.commonlib.dto.job;

import com.kaveks.commonlib.domain.job.SalaryPeriod;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record SalaryRangeUpdateRequest(

        @DecimalMin(value = "0.0", inclusive = true)
        BigDecimal minAmount,

        @DecimalMin(value = "0.0", inclusive = true)
        BigDecimal maxAmount,

        String currency,

        SalaryPeriod period

) {}