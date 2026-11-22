package com.climateatlas.dto;

import org.springframework.batch.core.JobExecution;
import java.time.LocalDateTime;

/** Subconjunto legible de los metadatos de una ejecución de Spring Batch. */
public record JobExecutionResponse(
        Long executionId, String jobName, String status,
        LocalDateTime startTime, LocalDateTime endTime
) {
    public static JobExecutionResponse from(JobExecution execution) {
        return new JobExecutionResponse(execution.getId(),
                execution.getJobInstance() == null ? "weatherRefreshJob" : execution.getJobInstance().getJobName(),
                execution.getStatus().name(), execution.getStartTime(), execution.getEndTime());
    }
}
