package com.climateatlas.app.dto;

import org.springframework.batch.core.job.JobExecution;
import java.time.LocalDateTime;

/**
 * DTO con la información pública de una ejecución Batch; evita exponer el objeto interno de Spring Batch.
 *  * @param executionId Identificador único de esta ejecución.
 *  * @param jobName Nombre del Job que se lanzó.
 *  * @param status Estado de la ejecución, por ejemplo STARTING o COMPLETED.
 *  * @param startTime Fecha y hora en que comenzó.
 *  * @param endTime Fecha y hora en que terminó; puede ser null mientras sigue ejecutándose.
 */
public record JobExecutionResponse(
        Long executionId, String jobName, String status,
        LocalDateTime startTime, LocalDateTime endTime
) {
    /**
     * Extrae del objeto JobExecution los campos necesarios para informar al cliente REST del Job ejecutado.
     */
    public static JobExecutionResponse from(JobExecution execution) {
        return new JobExecutionResponse(execution.getId(),
                execution.getJobInstance().getJobName(), execution.getStatus().name(),
                execution.getStartTime(), execution.getEndTime());
    }
}
