package com.climateatlas.dto;

import org.springframework.batch.core.job.JobExecution;
import java.time.LocalDateTime;

/** Subconjunto legible de los metadatos de una ejecución de Spring Batch 6. */
public record JobExecutionResponse(
        Long executionId, String jobName, String status,
        LocalDateTime startTime, LocalDateTime endTime
) {
    public static JobExecutionResponse from(JobExecution execution) {
        return new JobExecutionResponse(
                execution.getId(),
                execution.getJobInstance().getJobName(),
                execution.getStatus().name(),
                execution.getStartTime(),
                execution.getEndTime()
        );
    }
}
