package com.climateatlas.service;

import com.climateatlas.dto.JobExecutionResponse;
import com.climateatlas.exception.WeatherProviderException;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Arranca el Job desde el endpoint manual o el scheduler.
 * Spring Batch 6 recomienda JobOperator.start(Job, JobParameters) en lugar
 * del JobLauncher.run, que quedó obsoleto.
 */
@Service
public class WeatherJobService {

    private final JobOperator jobOperator;
    private final Job weatherRefreshJob;

    public WeatherJobService(
            JobOperator jobOperator,
            @Qualifier("weatherRefreshJob") Job weatherRefreshJob) {
        this.jobOperator = jobOperator;
        this.weatherRefreshJob = weatherRefreshJob;
    }

    public JobExecutionResponse start() {
        JobParameters parameters = new JobParametersBuilder()
                .addLong("requestedAt", System.currentTimeMillis())
                .toJobParameters();
        try {
            JobExecution execution = jobOperator.start(weatherRefreshJob, parameters);
            return JobExecutionResponse.from(execution);
        } catch (Exception exception) {
            throw new WeatherProviderException(
                    "No fue posible iniciar el proceso de actualización meteorológica.", exception);
        }
    }
}
