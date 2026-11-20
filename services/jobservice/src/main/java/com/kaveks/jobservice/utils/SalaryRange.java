package com.kaveks.jobservice.utils;

import com.kaveks.commonlib.domain.job.SalaryPeriod;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryRange {

    @Column(name = "salary_min", precision = 15, scale = 2)
    private BigDecimal minAmount;

    @Column(name = "salary_max", precision = 15, scale = 2)
    private BigDecimal maxAmount;

    @Column(name = "salary_currency", length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "salary_period", length = 20)
    private SalaryPeriod period;
}
