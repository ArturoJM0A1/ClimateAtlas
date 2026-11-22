package com.climateatlas.app.dto;

import org.springframework.batch.core.job.JobExecution;
import java.time.LocalDateTime;

/** DTO con la información pública de una ejecución Batch. */
public record JobExecutionResponse(
        Long executionId, String jobName, String status,
        LocalDateTime startTime, LocalDateTime endTime
) {
    public static JobExecutionResponse from(JobExecution execution) {
        return new JobExecutionResponse(execution.getId(),
                execution.getJobInstance().getJobName(), execution.getStatus().name(),
                execution.getStartTime(), execution.getEndTime());
    }
}
