package com.climateatlas.app.batch;

import com.climateatlas.app.dto.JobExecutionResponse;
import com.climateatlas.negocio.exception.WeatherProviderException;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/** Caso de uso de app que inicia el Job para el endpoint o el scheduler. */
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
