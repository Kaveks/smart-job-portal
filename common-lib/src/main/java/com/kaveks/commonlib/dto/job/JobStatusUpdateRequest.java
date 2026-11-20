
package com.kaveks.commonlib.dto.job;

import com.kaveks.commonlib.domain.job.JobStatus;
import jakarta.validation.constraints.NotNull;

public record JobStatusUpdateRequest(
        @NotNull(message = "Job status is required")
        JobStatus status
) {}

