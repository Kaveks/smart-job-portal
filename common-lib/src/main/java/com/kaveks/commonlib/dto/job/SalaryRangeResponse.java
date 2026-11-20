package com.kaveks.commonlib.dto.job;

import com.kaveks.commonlib.domain.job.SalaryPeriod;

import java.math.BigDecimal;

public record SalaryRangeResponse(
        BigDecimal minAmount,
        BigDecimal maxAmount,
        String currency,
        SalaryPeriod period
) {}